//
// Scaled VCS Project Support - VCS support for Scaled project framework.
// http://github.com/scaled/vcs-project/blob/master/LICENSE

package scaled.project

import java.nio.file.Path
import scaled._

/** A plugin that handles VCS integration. */
abstract class VCSPlugin extends AbstractPlugin {

  /** The string which identifies this VCS. For example: `git`, `hg`, `svn`. */
  def id :String

  /** Returns true if this VCS plugin applies to the project at `root`. */
  def applies (root :Path) :Boolean

  /** Returns the revision for the commit that last touched `line` of `path`, or `None`. */
  def blame (path :Path, line :Int) :Future[Option[String]]

  /** Obtains the commit message for the specified `revision`.
    * If no message can be obtained an error message can be returned, or the empty seq. */
  def commitMessage (root :Path, revision :String) :Future[Seq[String]]

  /** Obtains the commit message and diff for the specified `revision`.
    * If no diff can be obtained an error message can be returned, or the empty seq. */
  def commitDiff (root :Path, revision :String) :Future[Seq[String]]
}

object NOOPPlugin extends VCSPlugin {
  override def id = "none"
  override def applies (root :Path) = false
  override def blame (path :Path, line :Int) =
    Future.success(None)
  override def commitMessage (root :Path, revision :String) =
    Future.success(Seq("No known VCS in this project."))
  override def commitDiff (root :Path, revision :String) =
    Future.success(Seq("No known VCS in this project."))
}
