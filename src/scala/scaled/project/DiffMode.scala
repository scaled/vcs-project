//
// Scaled - a scalable editor extensible via JVM languages
// http://github.com/scaled/scaled/blob/master/LICENSE

package scaled.project

import scaled._
import scaled.code.{CodeConfig, Commenter}
import scaled.grammar.{Grammar, GrammarConfig, GrammarCodeMode}

/** Provides configuration for [[VCSDiffMode]]. */
object DiffConfig extends Config.Defs {
  import CodeConfig._
  import GrammarConfig._

  // map TextMate grammar scopes to Scaled style definitions
  val effacers = List(
    effacer("comment.line", commentStyle),
    effacer("comment.block", docStyle),

    // TODO: hacky, define real styles
    effacer("markup.inserted", stringStyle),
    effacer("markup.changed", constantStyle),
    effacer("markup.deleted", typeStyle),
    effacer("meta.diff.header", docStyle),
    effacer("meta.diff.index", constantStyle),
    effacer("meta.diff.commit", variableStyle),
    effacer("punctuation.definition.to-file", stringStyle),
    effacer("markup.changed", constantStyle),
    effacer("punctuation.definition.from-file", typeStyle)
  )

  val grammars = resource(Seq("Diff.ndf"))(Grammar.parseNDFs)
}

@Major(name="diff", desc="Displays diff output.")
class DiffMode (env :Env) extends GrammarCodeMode(env) {

  override def configDefs = DiffConfig :: super.configDefs
  override def grammars = DiffConfig.grammars.get
  override def effacers = DiffConfig.effacers

  override val commenter = new Commenter() {
    override def linePrefix  = "#"
  }
}
