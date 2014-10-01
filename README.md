distribute-calculation
======================

A Scala project which can do distribute calculation in one or more computer by using akka actors.
Master.scala is used as the main entrance of the program, which run on only one computer. In Master program I define an actor which name is “localactor”.  This actor will divide the workload into several work unit and send them to different remote actor. Remote actor will use the config in the Master.scala. The config define the localactor’s IP , the port it is listening  and all the IP and ports of remote actors.
Worker.scala is used as the worker to do the real work. It define a worker actor which is also corresponding  the remote actor defined in Master.scala. It will run on several different computers and use the IP of the computer when configure the file and listen to the port defined in config val. When receive the work request, the worker actor will start to calculate the work unit and then send back the result to the Master(the localactor).
And in Master.scala, the localactor also responds for collect the result. When the actor find that all the remote works send the result back. The local actor will print the result and shun down the system.
//////////////////////////////////////////////////////////////////
There are two version :
  One is working on a single computer and use multi-threaded,called multiT.scala
  One is working on network, one computer control other computers. Master.scala used on master computer and Worker.scala used on all the actor computer
