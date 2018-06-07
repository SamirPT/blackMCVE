package modules.push;

import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;

/**
 * Created by arkady on 01/03/16.
 */
public class PushModule extends Module {
	@Override
	public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
		return seq(bind(Push.class).to(PushImpl.class).eagerly());
	}
}
