
import akka.kernel.Bootable
import com.typesafe.config.ConfigFactory
import scala.util.Random
import akka.actor._
import java.net.InetAddress
import akka.routing.RoundRobinRouter
import akka.actor.{ Address, AddressFromURIString }
import akka.remote.routing.RemoteRouterConfig

sealed trait Squaremsg
  case class Work(workID:BigInt,workunit: BigInt, k: BigInt, N:BigInt)extends Squaremsg  
  case class Calculate(N:BigInt,k:BigInt,nofworker:BigInt,workunit:BigInt) extends Squaremsg
  case class Result(value:String) extends Squaremsg


class Master(N:BigInt,k:BigInt,nofworker: BigInt,workunit:BigInt) extends Bootable {
  val hostname=InetAddress.getLocalHost.getHostName
  val config=ConfigFactory.parseString(
     s"""
     akka{
       loglevel="ERROR"
       actor{
         provider="akka.remote.RemoteActorRefProvider"
        }
     remote{
        enabled-transports=["akka.remote.netty.tcp"]
        netty.tcp{
           hostname="192.168.0.103"
           //port=27672
                }
            }
       }
      akka{
           actor{
              deployment{
                 /ractor1{
                     remote="akka.tcp://WorkerApplication@lin114-01.cise.ufl.edu:27672"
                                    }
                
                /ractor2{
                     remote="akka.tcp://WorkerApplication@lin114-02.cise.ufl.edu:27672"
                                    }
                /ractor3{
                     remote="akka.tcp://WorkerApplication@lin114-03.cise.ufl.edu:27672"
                                    }
                /ractor4{
                     remote="akka.tcp://WorkerApplication@lin114-04.cise.ufl.edu:27672"
                                    }
                 /ractor5{
                     remote="akka.tcp://WorkerApplication@lin114-05.cise.ufl.edu:27672"
                                    }
                 /ractor6{
                     remote="akka.tcp://WorkerApplication@lin114-06.cise.ufl.edu:27672"
                                    }
                 /ractor7{
                     remote="akka.tcp://WorkerApplication@lin114-07.cise.ufl.edu:27672"
                                    }
                 /ractor8{
                     remote="akka.tcp://WorkerApplication@lin114-08.cise.ufl.edu:27672"
                                    }
                 /ractor9{
                     remote="akka.tcp://WorkerApplication@lin114-09.cise.ufl.edu:27672"
                                    }
                 /ractor10{
                     remote="akka.tcp://WorkerApplication@lin114-10.cise.ufl.edu:27672"
                                    }
     
     
                         }
                 }
     remote.netty.tcp.port=27672
          }
     """
  )
  println("before config")
   val system =ActorSystem("RemoteCreation", ConfigFactory.load(config))
   println("system")
     val remoteActor1 = system.actorOf(Props[Worker], name = "ractor1") 
     val remoteActor2= system.actorOf(Props[Worker], name = "ractor2")
     val remoteActor3 = system.actorOf(Props[Worker], name = "ractor3") 
     val remoteActor4= system.actorOf(Props[Worker], name = "ractor4")
     val remoteActor5 = system.actorOf(Props[Worker], name = "ractor5") 
     val remoteActor6= system.actorOf(Props[Worker], name = "ractor6")
     val remoteActor7 = system.actorOf(Props[Worker], name = "ractor7") 
     val remoteActor8= system.actorOf(Props[Worker], name = "ractor8")
     val remoteActor9 = system.actorOf(Props[Worker], name = "ractor9") 
     val remoteActor10= system.actorOf(Props[Worker], name = "ractor10")
    
    val routees=Vector[String]("/user/ractor1","/user/ractor2","/user/ractor3","/user/ractor4","/user/ractor5","/user/ractor6","/user/ractor7","/user/ractor8","/user/ractor9","/user/ractor10")

    val router=system.actorOf(Props.empty.withRouter(RoundRobinRouter(routees=routees)))

     val localActor = system.actorOf(Props(classOf[CreationActor], router), name = "creationActor")
 
     println("local")
  //#setup
  def doSomething(N:BigInt) ={
 
 
        var sN:BigInt=N
        var sk=k
        var snofworker=nofworker
        var sworkunit=workunit
         
         println("send to localActor")
        
         localActor ! Calculate(sN,sk,snofworker,sworkunit)

  }
  //#setup

  def startup() {
  }

  def shutdown() {
    system.shutdown()
  }
}

//#actor
class CreationActor(router:ActorRef) extends Actor {

       var nofunit:BigInt=10
 
       var finalresult:String=" "
        //count 
        var nrOfResults:BigInt = 0  
        //var pp:BigInt
        var finalsputime:Double=0
        var wstart:BigInt=0
 
        def receive = {  
     
            case Calculate(sN,sk,snofworker,sworkunit) =>  println("receive caculate")  
                                                             for (workid:BigInt <- wstart until nofunit) 
                                                              {println(workid)
                                                                   router! Work(workid,sworkunit,sk,sN)}

            
            case Result(value) =>   
                                 println("receive result")
                                  finalresult=finalresult +value   
                                  nrOfResults += 1  
                                  //all result back                   
                                  if(nrOfResults == nofunit)   
                                        {   
                                           println(finalresult)  
                                           context.system.shutdown()   
                                         }    
                     }   

}

//#actor
class Worker extends Actor {
  def receive = {
    case "2"=>println("123")
                      
  }
}
object bonusproject {
  def main(args: Array[String]) {  
       var N:BigInt=1000000000
         if(args(0)=="10000000000")
           {var pp:BigInt=1000000000
          var N:BigInt=pp*10}
         else {var N:BigInt=args(0).toLong}
         
           val k:BigInt=args(1).toInt
                    
           var nofworker=10
         val workunit:BigInt=N/(nofworker)
         println("Started Creation Application")
         val app = new Master(N,k,nofworker,workunit)
    
         app.doSomething(N)         
    
  }
}
