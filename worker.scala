
import akka.kernel.Bootable
import akka.actor.{ Props, Actor, ActorSystem }
import com.typesafe.config.ConfigFactory
import java.net.InetAddress

  sealed trait Squaremsg
  case class Work(workID:BigInt,workunit: BigInt, k: BigInt, N:BigInt)extends Squaremsg   
  case class Result(value:String) extends Squaremsg
  


class Worker extends Actor {
        def searchnum(bstart:BigInt,bend:BigInt,k:BigInt,sumN:BigInt) = {   
            var acc:String=""   
            var bbend:BigInt=bend
            if (bbend>sumN) 
                  {bbend=sumN}  
//             println("start jisuan")      
            for (i <- bstart to bbend)   
            { 
              var sqursum:Long=0
              for(j:BigInt<- i until i+k){
                var longj=j.toLong
                var mycash:Long=longj*longj
                 sqursum+=mycash
                  }
              var qq:Double=scala.math.sqrt(sqursum)            
              if(qq %1==0)
                acc=acc+" "+i                 
               }  
             
            acc   
        }   
  
        def receive = {      
            case Work(wid,wstart,wk,sumN)  => 
              			println("Received work from master");                          
                        var acc=searchnum(wstart*wid+1,wstart*(wid+1),wk,sumN) 
                          sender !  Result(acc)        
                          println("Sent result back to master")
        }   
    }   

class WorkerApplication extends Bootable{
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
           hostname="192.168.0.111"
           port=27672
                }
            }
       }
     akka{
     	#LISTEN on tcp port 27672
     	remote.netty.tcp.port = 27672
     }
     """
  )
  //#setup
  val system = ActorSystem("WorkerApplication",
    ConfigFactory.load(config))
    println("System created")
  //#setup

  def startup() {
  }

  def shutdown() {
    system.shutdown()
  }
}

object CalcApp {
  def main(args: Array[String]) {
    new WorkerApplication
    println("Started Calculator Application - waiting for messages")
  }
}

