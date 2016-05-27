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
    bind("vcs-blame-diff", "C-c C-b C-c");

  //
  // Fns

  @Fn("Displays the commit message that last touched the line at the point.")
  def vcsBlameCommit () :Unit = buffer.store.file match {
    case None => throw Errors.feedback(s"Buffer not visiting file? ${buffer.store}")
    case Some(path) =>
      val line = view.point().row
      vcs.blame(path, line) match {
        case None => throw Errors.feedback(
          s"Unable to get '${vcs.id}' blame info for ${path.getFileName}:$line")
        case Some(rev) => view.popup() = Popup.text(
          vcs.commitMessage(root, rev), Popup.UpRight(view.point()))
      }
  }
}
