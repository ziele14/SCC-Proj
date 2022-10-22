//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package scc.srv;

import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class MainApplication extends Application {
	private Set<Object> singletons = new HashSet();
	private Set<Class<?>> resources = new HashSet();

	public MainApplication() {
		this.resources.add(UserResource.class);
		this.resources.add(ControlResource.class);
		this.resources.add(MediaResource.class);
		this.singletons.add(new MediaResource());
		this.resources.add(AuctionResource.class);
	}

	public Set<Class<?>> getClasses() {
		return this.resources;
	}

	public Set<Object> getSingletons() {
		return this.singletons;
	}
}
