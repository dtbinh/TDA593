package chalmers.tda593.y2018.g11.rovu.control_station;

import chalmers.tda593.y2018.g11.rovu.control_station.storage.StorageBroker;
import chalmers.tda593.y2018.g11.rovu.model.Area;
import chalmers.tda593.y2018.g11.rovu.model.Status;

import java.util.Collection;

/**
 * Created by svante on 2018-12-01.
 */
class PointRewarder implements Runnable {
    private Procedure current;

    PointRewarder() {
        this.current = new ProcedureA(); //Defaulting to procedure A when initiating class, can be changed later
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }


    /**
     * Calculates and returns the amount of points that the current state of all robots
     * should reward depending on where in the environment they reside.
     */
    private int calculate(Collection<Status> robots, Collection<Area> logicalAreas, Collection<Area> physicalAreas) {
        return current.calculate(robots, logicalAreas, physicalAreas);
    }

    private void updateStoragePoints(Collection<Status> robots, Collection<Area> logicalAreas, Collection<Area> physicalAreas) {
        StorageBroker.getRewardDAO().addReward(calculate(robots, logicalAreas, physicalAreas));
    }

    /**
     * Calculates and adds points to the storage if the requirements are met.
     * Updates the current running procedure if the requirements are met.
     */
    @Override
    public void run() {
        try {
            Thread.sleep(20000);
            updateStoragePoints(StorageBroker.getStatusDAO().getStatuses(), StorageBroker.getMapDAO().getEnvironment().getLogicalAreas(),
		            StorageBroker.getMapDAO().getEnvironment().getPhysicalAreas());
            current.update(StorageBroker.getStatusDAO().getStatuses(), StorageBroker.getMapDAO().getEnvironment().getLogicalAreas(),
		            StorageBroker.getMapDAO().getEnvironment().getPhysicalAreas());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
