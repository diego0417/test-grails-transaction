# test-grails-transaction

El proposito de este proyecto es comparar la forma de guardar un dominio en grails. Se comparo Domain.save(flush:true) vs Domain.withTransaction{domain.save()}

La intención es mostrar como queda el Pool de Conexiones a la DB luego de utilizar uno de los dos métodos mensionados. Para lograr esto se modifico las propiedades del DataSource de la siguiente manera:

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
                   maxAge = 10 * 1000
                }
            }
    }

  De esta forma tenemos como maxímo 2 conexiones a la DB dentro del pool

Testing
---
Clonamos el repo en GitHub y luego ejecutamos los commandos de Grails

    git clone git@github.com:fcambarieri/test-grails-transaction.git
    grails clean
    grails compile
    grails run-app

Si queremos probar el save nos abrimos 3 terminales y ejecuamos el siguiente curl en cada uno de ellos

    curl -i -X POST -H "Content-Type:application/json" "localhost:8080/opa/testingPoolSize" -d {"trx_id":2222}

El resultado es el siguiente:

  Los dos primeros request que entren van a tener retenida la conexión del Pool y no va a liberarla y el 3er
  request va a tirar una excepción como a continuación

    {

      "message": "Hibernate operation: could not prepare statement; uncategorized SQLException for SQL [insert into transaction (id, version, date_created, status, trx_id) values (null, ?, ?, ?, ?)]; SQL state [null]; error code [0]; [http-bio-8080-exec-6] Timeout: Pool empty. Unable to fetch a connection in 1 seconds, none available[size:2; busy:2; idle:0; lastwait:1000].; nested exception is org.apache.tomcat.jdbc.pool.PoolExhaustedException: [http-bio-8080-exec-6] Timeout: Pool empty. Unable to fetch a connection in 1 seconds, none available[size:2; busy:2; idle:0; lastwait:1000].",

      "cause": "[http-bio-8080-exec-6] Timeout: Pool empty. Unable to fetch a connection in 1 seconds, none available[size:2; busy:2; idle:0; lastwait:1000]."
    }

  Esto ocurre por el domain.save(flush:true) NO devuelve la conexión al Datasource.

  ¿Como lo solucionamos?

  Cambiando el domain.save(flush:true) port

    DomainClass.withTransaction{
      domainInstance.save()
    }

  Si ahora en las terminales abiertas ejecutamos el siguiente curl

    curl -i -X POST -H "Content-Type:application/json" "localhost:8080/opa/testingPoolSizeWithTransaction" -d {"trx_id":2222}

  Los tres request en esta ocación van a terminar correctamente, ya que el withTransaction devuelve la conexión al pool.
