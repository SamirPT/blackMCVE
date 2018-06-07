package modules.fb;

import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;

/**
 * Created by arkady on 09/04/16.
 */
public class FBModule extends Module {

	@Override
	public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
		return seq(bind(FB.class).to(FBImpl.class));
	}
}
