//
// Scaled VCS Project Support - VCS support for Scaled project framework.
// http://github.com/scaled/vcs-project/blob/master/LICENSE

package scaled.project

import scaled._
import scaled.util.Errors

@Minor(name="vcs", stateTypes=Array(classOf[Project]), desc="""Provides VCS fns.""")
class VCSMode (env :Env) extends MinorMode(env) {

  val project = Project(buffer)
  val root = project.root.path
  lazy val vcs = env.msvc.service[PluginService].resolvePlugins[VCSPlugin]("vcs").plugins.
    find(_.applies(root)) getOrElse NOOPPlugin

  override def keymap = super.keymap.
    bind("vcs-blame-commit", "C-c C-b C-c").
    bind("vcs-blame-diff", "C-c C-b C-d");

  //
  // Fns

  @Fn("Displays the commit message that last touched the line at the point.")
  def vcsBlameCommit () {
    val rev = blameRevAt(view.point())
    view.popup() = Popup.text(vcs.commitMessage(root, rev), Popup.UpRight(view.point()))
  }

  @Fn("Displays the commit message that last touched the line at the point.")
  def vcsBlameDiff () {
    val rev = blameRevAt(view.point())
    val diffs = vcs.commitDiff(root, rev)
    val state = project.bufferState("diff")
    val diffbuff = wspace.createBuffer(Store.scratch(s"*${vcs.id}: $rev", buffer.store), state)
    diffbuff.append(diffs map Line.apply)
    window.focus.visit(diffbuff)
  }

  private def blameRevAt (loc :Loc) :String = buffer.store.file match {
    case None => throw Errors.feedback(s"Buffer not visiting file? ${buffer.store}")
    case Some(path) => vcs.blame(path, loc.row+1) match {
      case None => throw Errors.feedback(
        s"Unable to get '${vcs.id}' blame info for ${path.getFileName}:${loc.row+1}")
      case Some(rev) => rev
    }
  }
}
