class BootStrap {

    def grailsApplication
    def sessionFactory

    def init = { servletContext ->
      grailsApplication.serviceClasses.each {
  	     def releaseSession = it.getPropertyValue('releaseSession')
  	     if ( releaseSession == true ) {
  	      // example of how to 'intercept' service classes.
  	      it.metaClass.invokeMethod = { name, args ->
              try {
                def session = sessionFactory.getCurrentSession()
     				    session.close()
              } catch(Exception e) {

              }

  	       		return delegate.metaClass.getMetaMethod(name,args).invoke( delegate, args)
  	      }
  	     }
      }
    }
    def destroy = {
    }
}
