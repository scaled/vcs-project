//
// Scaled VCS Project Support - VCS support for Scaled project framework.
// http://github.com/scaled/vcs-project/blob/master/LICENSE

package scaled.project

import org.junit._
import org.junit.Assert._
import scaled._

class IOTest {

  @Test def testOverflowBuffer () {
    val buffer = new IO.RollingBuffer(500)
    for (ii <- 0 to 1000) {
      buffer.append(s"Line $ii")
    }
    val out = buffer.toSeq
    assertEquals(501, out.size)
    assertEquals("Line 0", out.head)
    assertEquals("Line 1000", out.last)
  }

  @Test def testShort () {
    val buffer = new IO.RollingBuffer(500)
    for (ii <- 0 to 100) {
      buffer.append(s"Line $ii")
    }
    val out = buffer.toSeq
    assertEquals(101, out.size)
    assertEquals("Line 0", out.head)
    assertEquals("Line 100", out.last)
  }

  @Test def testReallyShort () {
    val buffer = new IO.RollingBuffer(500)
    for (ii <- 0 to 10) {
      buffer.append(s"Line $ii")
    }
    val out = buffer.toSeq
    assertEquals(11, out.size)
    assertEquals("Line 0", out.head)
    assertEquals("Line 10", out.last)
  }

  @Test def testExec  () {
    var res = IO.exec(Seq("ls", "/"))
    assertEquals(0, res.exitCode)
    assertTrue(res.stdout.size > 0)
    assertEquals(0, res.stderr.size)
  }
}
