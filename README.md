# test-grails-transaction

El proposito de este proyecto es comparar la forma en que se guardar un dominio en grails mirando el estado del pool de conexiones a la DB. Comparamos Domain.save(flush:true) vs Domain.withTransaction{domain.save()}

Para lograr nuestro objetivo modificamos las propiedades del DataSource para el ambiente "development" se la siguiente forma:

    development {
            dataSource {
                dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
                url = "jdbc:h2:prodDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
                properties {
                   jmxEnabled = true
                   initialSize = 1
                   maxActive = 2
                   minIdle = 1
                   maxIdle = 2
                   maxWait = 1000
                }
            }
    }

  Entonces vamos a tener como máximo 2 conexiones a la DB y un tiempo de espera por una conexión de 1 segundo.
  
  En el servicio TransactionService tenemos varios métodos de testing pero nos vamos a concentrar en dos testingPoolSize y testingPoolSizeWithTransaction.
  
     def testingPoolSize(Transaction trx) {
      trx.dateCreated = new Date()
      if (!trx.save(flush:true)){
        throw new RuntimeException(trx.errors.toString())
      }
      Thread.sleep(1000 * 10)
      return trx
    }
    
    def testingPoolSizeWithTransaction(Transaction trx) {
      trx.dateCreated = new Date()
      Transaction.withTransaction {
          trx.save()
      }
      Thread.sleep(1000 * 10)
      return trx
    }
 
 En ambos casos luego de hacer el save() ponemos un Thread.sleep() para simular un tiempo de espera y así poder ver como se comparta el Pool de conexiones.

Testing
---
Clonamos el repo en GitHub y luego ejecutamos los commandos de Grails

    git clone git@github.com:fcambarieri/test-grails-transaction.git
    cd test-grails-transaction
    grails clean
    grails compile
    grails run-app

Para probar el save(flush:true) abrimos 3 terminales y ejecuamos el siguiente curl en cada uno de ellos

    curl -i -X POST -H "Content-Type:application/json" "localhost:8080/opa/testingPoolSize" -d {"trx_id":2222}

El resultado es el siguiente:

  Los dos primeros request van a crear una Transaction, sin liberar conexión ni retornarla al Pool. El resultado es el siguiente:
  
  Los dos primeros request va a responder con
  
    {"id":2,"status":"pending","date":"2015-10-28T12:59:29Z"}
  
  y el 3er request va a tirar una excepción como a continuación

    {

      "message": "Hibernate operation: could not prepare statement; uncategorized SQLException for SQL [insert into transaction (id, version, date_created, status, trx_id) values (null, ?, ?, ?, ?)]; SQL state [null]; error code [0]; [http-bio-8080-exec-6] Timeout: Pool empty. Unable to fetch a connection in 1 seconds, none available[size:2; busy:2; idle:0; lastwait:1000].; nested exception is org.apache.tomcat.jdbc.pool.PoolExhaustedException: [http-bio-8080-exec-6] Timeout: Pool empty. Unable to fetch a connection in 1 seconds, none available[size:2; busy:2; idle:0; lastwait:1000].",

      "cause": "[http-bio-8080-exec-6] Timeout: Pool empty. Unable to fetch a connection in 1 seconds, none available[size:2; busy:2; idle:0; lastwait:1000]."
    }

  Esto ocurre por el domain.save(flush:true) NO devuelve la conexión al Datasource de forma inmediata, recién cuando el request finaliza libera la conexión.

  ¿Como lo solucionamos?

  Cambiando el trx.save(flush:true) por

    Transaction.withTransaction {
          trx.save()
    }

  Si ahora en las terminales abiertas ejecutamos el siguiente curl

    curl -i -X POST -H "Content-Type:application/json" "localhost:8080/opa/testingPoolSizeWithTransaction" -d {"trx_id":2222}

  Los tres request van a terminar correctamente, ya que el withTransaction si devuelve la conexión al pool.
