package modules.s3;

import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;

/**
 * Created by arkady on 01/03/16.
 */
public class S3Module extends Module {
	@Override
	public Seq<Binding<?>> bindings(Environment environment, Configuration configuration) {
		return seq(bind(S3.class).to(S3Impl.class));
	}
}
