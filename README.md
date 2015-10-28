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
    
curl -i -X POST -H "Content-Type:application/json" "localhost:8080/opa/testingPoolSize" -d {"trx_id":2222}

curl -i -X POST -H "Content-Type:application/json" "localhost:8080/opa/testingPoolSizeWithTransaction" -d {"trx_id":2222}
