package test.config

class SessionUtilService {

   static transactinal = false
   
   def sessionFactory

   def getCurrentSession() {
     return sessionFactory.getCurrentSession()
   }

   def closeSession() {
     getCurrentSession().disconnect()
   }

}
