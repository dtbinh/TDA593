package robot.routines;

import model.Status;
import robot.routines.actions.Action;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class RobotFailureRoutine implements Routine {
	@Override
	public Action calculateAction(Status status) {
		throw new NotImplementedException();
	}
}
