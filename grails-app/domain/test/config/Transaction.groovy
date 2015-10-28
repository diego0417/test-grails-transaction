package test.config

class Transaction {

    String trx_id
    String status = "pending"
    Date dateCreated

    static constraints = {
    }
}
