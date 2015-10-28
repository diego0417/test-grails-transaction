package test.config

import grails.converters.JSON
import utils.DataSourcePoolUtil

class OpaController {

  def transactionService

  def create() {
    def body = request.getJSON()
    Transaction trx = new Transaction(body)
    trx.dateCreated = new Date()
    def id = DataSourcePoolUtil.printPoolInfo(log, "createWithFlush", {transactionService.createWithFlush(trx)})
    return [
      response	: [id:id],
      status		: 201
    ]
  }

  def createWithTransaction() {
    def body = request.getJSON()
    Transaction trx = new Transaction(body)
    trx.dateCreated = new Date()
    def id = DataSourcePoolUtil.printPoolInfo(log, "createWithTransaction", {transactionService.createWithTransaction(trx)})
    return [
      response	: [id:id],
      status		: 201
    ]
  }

  def getTransaction() {
    def id = params.id
    def trx = DataSourcePoolUtil.printPoolInfo(log, "getTransaction", {return transactionService.getTransaction(id)})
    return [
      status: 200,
      response: [id:trx.id, date: trx.dateCreated]
    ]
  }

  def testingClosingSession() {
    def body = request.getJSON()

    Transaction trx = new Transaction(body)
    trx.dateCreated = new Date()
    trx = transactionService.testingClosingSession(trx)

    def json = [
      id:trx.id,
      status: trx.status,
      date: trx.dateCreated
    ] as JSON

    render json.toString()
  }

  def testingPoolSize() {
    try  {
      def body = request.getJSON()

      Transaction trx = new Transaction(body)
      trx.dateCreated = new Date()
      trx = transactionService.testingPoolSize(trx)

      def json = [
        id:trx.id,
        status: trx.status,
        date: trx.dateCreated
      ] as JSON

      render json.toString()
    } catch (Exception e) {
      Throwable t = e
      while(t.getCause() != null) {
        t = t.getCause()
      }
      def mapError = ["message":e.message, "cause":t?.message] as JSON
      render mapError.toString()
    }

  }

  def testingPoolSizeWithTransaction() {
    def body = request.getJSON()

    Transaction trx = new Transaction(body)
    trx.dateCreated = new Date()
    trx = transactionService.testingPoolSizeWithTransaction(trx)

    def json = [
      id:trx.id,
      status: trx.status,
      date: trx.dateCreated
    ] as JSON

    render json.toString()
  }

  def poolStatus() {
    def map = DataSourcePoolUtil.getPoolStatus() as JSON
    render map.toString()
  }
}
