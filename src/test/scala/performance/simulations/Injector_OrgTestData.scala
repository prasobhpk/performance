package performance.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import performance.simulations.lib.JenkinsParam._
import performance.simulations.scenarios._

import scala.concurrent.duration._

/**
  * Created by Tarun Kale.
  */

class Injector_OrgTestData extends Simulation {

  val httpTEST = http
    .baseURL(TEST_URL)
    //.proxy(Proxy("proxy-src.research.ge.com",8080))
    .acceptHeader("text/html,application/xhtml+xml,application/xml:q=0.9,image/webp,*/*:q-0.8")
    .acceptLanguageHeader("en-US,en;q=0.8")
    //.connection("""keep-alive""")


  //Instantiating PostNotification
  val testDataRequests = new TestData

  before {
    println("Starting LOAD test, Targeting POSTS only at  "+ peakRPS  + " rps." + "Steady state = " + steadyTime )
  }


  setUp(

    // Load Injection
    testDataRequests.scnCreateOrganization.inject( rampUsersPerSec(1) to (peakRPS) during (rampTime seconds), constantUsersPerSec(peakRPS) during(steadyTime seconds)).protocols(httpTEST)

  ).assertions (

    details(testDataRequests.grpOrganization / "CreateOrganization" ).responseTime.mean.lte( meanResponseTime),
    details(testDataRequests.grpOrganization / "CreateOrganization" ).failedRequests.percent.lte( errorRate),
    details(testDataRequests.grpOrganization / "CreateWorkplace" ).responseTime.mean.lte( meanResponseTime),
    details(testDataRequests.grpOrganization / "CreateWorkplace" ).failedRequests.percent.lte( errorRate)

  )

  after {
    println("Completed Gatling test")
  }

}