package user_interface;
import control_station.OperatorInterface;
import model.*;

import java.util.*;

public class Display {
    private int points;
    private Scanner input = new Scanner(System.in);
    private List<Coordinate> missionList = new ArrayList<>();
    private MissionComposer missionComp = new MissionComposer();
    private Mission mission;
    private OperatorInterface operatorInterface;

    public Display(OperatorInterface operatorInterface){
        this.operatorInterface = operatorInterface;
    }

    public void displayView(){
	    System.out.printf(
	    		"#######################################################################%n" +
			    "Program started! You can use this terminal to control your robots.%n" +
			    "See \"help\" for available commands%n" +
			    "\"exit\" to quit program. %n");
    	while (true) {
    		System.out.printf("> ");
		    String[] parts = input.nextLine().split(" ");

		    if(parts.length > 1){
		    	int robot = Integer.parseInt(parts[1]);
		    	switch (parts[0]){
					case "start":
						// Todo handle invalid id
						System.out.printf("Starting robot %s%n", robot);
						operatorInterface.assignAction(robot, new EmergencyInstruction(false));
						break;
					case "stop":
						// Todo handle invalid id
						System.out.printf("Stopping robot %s%n", robot);
						operatorInterface.assignAction(robot, new EmergencyInstruction(true));
						break;
					case "robot":
						// Todo handle invalid id
						System.out.printf("Robot %s selected, \"q\" to deselect%n", robot);
						while (true) {
							System.out.printf("Robot %s > ", robot);
							parts = input.nextLine().split(" ");

							if (parts.length > 0) {
								switch (parts[0]) {
									case "start":
										System.out.printf("Starting robot %s%n", robot);
										operatorInterface.assignAction(robot, new EmergencyInstruction(false));
										break;
									case "stop":
										System.out.printf("Stopping robot %s%n", robot);
										operatorInterface.assignAction(robot, new EmergencyInstruction(true));
										break;
									case "color":
									case "colour":
										System.out.printf("Changing colo(u)r of robot %s%n", robot);
										operatorInterface.assignAction(robot, new ChangeColourInstruction());
										break;
									case "mission":
										System.out.println("Add a coordinate within the allowed range.");
										System.out.println("Write 'done' when all desired mission points are input.");

										Strategy choice = Strategy.GIVEN_ORDER;
										if (parts.length > 1){
											switch (parts[1]){
												case "shortest":
													choice = Strategy.SHORTEST_ROUTE;
													break;
												case "given":
													choice = Strategy.GIVEN_ORDER;
													break;
												case "backwards":
													choice = Strategy.BACKWARDS;
													break;
												case "random":
													choice = Strategy.RANDOM;
													break;
											}
										}

										float inputX;
										float inputY;
										String input = "";

										while (!(input.equals("no"))) {
											System.out.println("Enter coordinates one after the other"); // Shorten down to use float array.
											System.out.printf("> ");
											inputX = this.input.nextFloat();
											System.out.printf("> ");
											inputY = this.input.nextFloat();
											missionList.add(new Coordinate(inputX, inputY));
											System.out.print("Continue input? (yes/no)");
											System.out.printf("> ");
											this.input.nextLine();
											input = this.input.nextLine();
										}
										mission = missionComp.createMission(missionList, robot);
										operatorInterface.assignMission(mission,choice);
										missionList.clear();
										break;
									case "exit":
										System.exit(0);
										break;
									case "help":
										String[] commands = {
												"exit                                   - Quits application",
												"stop                                   - Stops the robot",
												"start                                  - Starts the robot",
												"mission {shortest|backwards|random}    - Sets a mission for the robot",
												"map                                    - show the current map",
												"q                                      - deselect the robot",
												"help                                   - show this help text",
												"colour || color                        - makes the robot change colo(u)r",
										};
										System.out.printf(String.join("%n", commands) + "%n");
										break;
									case "q":
										break;
									default:
										System.out.printf("Input wasn't recognized see \"help\" for available commands %n");
								}

								if (parts[0].equals("q"))
									break;
								
							} else {
								System.out.printf("Input wasn't recognized see \"help\" for available commands %n");
							}
						}
				}
			}
		    else if(parts.length > 0){
				switch (parts[0]){
					case "start":
						System.out.printf("Starting all robots%n");
						// TODO start all robots
						break;
					case "stop":
						System.out.printf("Stopping all robots %n");
						// TODO stop all robots
						break;
					case "map":
						printMap();
						break;
					case "points":
						printPoints();
						break;
					case "exit":
						System.exit(0);
						break;
					case "help":
						String[] commands = {
								"exit                 - Quits application",
								"stop                 - Stops all robots",
								"stop  {robotId}      - Stops the robot",
								"start                - Starts all robots",
								"start {robotId}      - Starts the robot",
								"robot {robotId}      - selects the robot",
								"robots               - get info about available robots",
								"map                  - show the current map",
								"points               - show the total amount of points rewarded",
								"help                 - show this help text",
						};
						System.out.printf(String.join("%n", commands) + "%n");
						break;
					case "robots":
						//TODO: print robots info
						break;
					default:
						System.out.printf("Input wasn't recognized see \"help\" for available commands %n");
				}
			}
	    }
    }

    private void printPoints(){
    	int reward = operatorInterface.getRewardPoints();
		System.out.println("The current total amount of points rewarded: " + reward);
	}

	private static final int RESOLUTION = 4;


	public void printMap(){
    	Environment map = operatorInterface.getEnv();
    	int mapWidth = (int) map.getWidth();
    	int mapHeight = (int) map.getHeight();
    	Collection<Status> robots = operatorInterface.getStatuses();
    	Collection<Wall> walls = map.getWalls();
		char[][] plot = new char[mapWidth*RESOLUTION][mapHeight*RESOLUTION];

		for(Wall w : walls){
			Coordinate start = w.getStart();
			Coordinate end = w.getEnd();
			int startX = (int)(start.getX() < end.getX() ? start.getX() : end.getX());
			int startY = (int)(start.getY() < end.getY() ? start.getY() : end.getY());
			int endX = (int)(start.getX() >= end.getX() ? start.getX() : end.getX());
			int endY = (int)(start.getY() >= end.getY() ? start.getY() : end.getY());

			if (startX == endX) {
				for(int j = startY * RESOLUTION; j < endY * RESOLUTION; j++){
					plot[startX * RESOLUTION][j] = '#';
				}
			} else if (startY == endY) {
				for(int i = startX * RESOLUTION; i < endX * RESOLUTION; i++){
					plot[i][startY * RESOLUTION] = '#';
				}
			} else {
				for(int i = startX * RESOLUTION; i < endX * RESOLUTION; i++){
					for(int j = startY * RESOLUTION; j < endY * RESOLUTION; j++){
						plot[i][j] = '#';
					}
				}
			}
		}


		for(Status s : robots){
			Coordinate location = s.getLocation();
			int x, y;
			x = (int) s.getLocation().getX();
			y = (int) s.getLocation().getY();
			plot[x * RESOLUTION][y * RESOLUTION] = Integer.toString(s.getId() % 10).charAt(0);
		}

		for (int i = -2; i < mapWidth * RESOLUTION;i++) System.out.print("#");
		System.out.println();

		for(int i = 0; i < plot[0].length;i++){
			System.out.print("#");
			for(int j = 0; j < plot.length;j++){
				if(plot[j][i] == 0){
					System.out.print(' ');
				}
				else{
					System.out.print(plot[j][i]);
				}
			}
			System.out.println("#");
		}
		for (int i = -2; i < mapWidth * RESOLUTION;i++) System.out.print("#");
		System.out.println();


		

	}

    public void updateView(){}

}
