package test.config

import grails.converters.JSON
import utils.DataSourcePoolUtil

class TransactionController {

    def transactionService

    def create() {
      def body = request.getJSON()
      Transaction trx = new Transaction(body)
      trx.dateCreated = new Date()
      def id = DataSourcePoolUtil.printPoolInfo(log, "createWithFlush", {transactionService.createWithFlush(trx)})
      return [
        response	: ([id:id] as JSON).toString(),
        status		: 201
      ]
    }

    def createWithTransaction() {
      def body = request.getJSON()
      Transaction trx = new Transaction(body)
      trx.dateCreated = new Date()
      def id = DataSourcePoolUtil.printPoolInfo(log, "createWithFlush", {transactionService.transactionService(trx)})
      return [
        response	: ([id:id] as JSON).toString(),
        status		: 201
      ]
    }

    def getTransaction() {
      def id = params.id
      def trx = DataSourcePoolUtil.printPoolInfo(log, "getTransaction", {return transactionService.getTransaction(id)})
      return [
        status: 200,
        response: [([id:trx.id, date: trx.dateCreated] as JSON).toString()]
      ]
    }

}
