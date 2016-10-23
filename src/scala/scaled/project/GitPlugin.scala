//
// Scaled - a scalable editor extensible via JVM languages
// http://github.com/scaled/scaled/blob/master/LICENSE

package scaled.project

import java.nio.file.{Files, Path}
import scala.sys.process._
import scaled._
import scaled.util.{Errors, Process}

@Plugin(tag="vcs")
class GitPlugin (editor :Editor) extends VCSPlugin {

  override def id = "git"

  override def applies (root :Path) = {
    // TODO: this is kind of hacky, how many levels up should we check?
    Files.exists(root.resolve(".git")) || Files.exists(root.getParent.resolve(".git")) ||
    Files.exists(root.getParent.getParent.resolve(".git"))
  }

  override def blame (path :Path, line :Int) = {
    val cmd = Seq("git", "blame", "-L", s"$line,$line", path.getFileName.toString)
    Process.exec(editor.exec, cmd, path.getParent).map(res => {
      if (res.exitCode == 0) Some(res.stdout(0).split(" ")(0))
      else throw Errors.feedback(fail(res, cmd).mkString("\n"))
    })
  }

  override def commitMessage (root :Path, revision :String) = {
    val cmd = Seq("git", "show", "-s", revision)
    Process.exec(editor.exec, cmd, root).map(res => {
      if (res.exitCode == 0) res.stdout
      else fail(res, cmd)
    })
  }

  override def commitDiff (root :Path, revision :String) = {
    val cmd = Seq("git", "show", revision)
    Process.exec(editor.exec, cmd, root, 10000).map(res => {
      if (res.exitCode == 0) res.stdout
      else fail(res, cmd)
    })
  }

  private def fail (res :Process.Result, cmd :Seq[String]) = {
    val sb = Seq.builder[String]()
    sb += s"git failed (${res.exitCode}):"
    sb += cmd.mkString("[", " ", "]")
    sb ++= res.stdout
    sb ++= res.stderr
    sb.build()
  }
}
