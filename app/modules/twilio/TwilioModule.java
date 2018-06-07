package modules.twilio;

import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;

/**
 * Created by arkady on 04/02/16.
 */
public class TwilioModule extends Module {
	@Override
	public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
		return seq(bind(Twilio.class).to(TwilioImpl.class));
	}
}
