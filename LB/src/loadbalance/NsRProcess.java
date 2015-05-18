package loadbalance;

import org.opensourcephysics.stp.randomwalk.randomwalk1.OneDimensionalWalkApp;
import org.opensourcephysics.stp.randomwalk.randomwalk1.OneDimensionalWalkWRApp;
import org.opensourcephysics.stp.randomwalk.randomwalk2.TwoDimensionalWalkApp;
import org.opensourcephysics.stp.randomwalk.saw.Flat;
import org.opensourcephysics.stp.randomwalk.saw.OneWalker;

/*************************************************************************
 *  Compilation:  javac RandomWalk.java
 *  Execution:    java RandomWalk N
 *  Dependencies: StdDraw.java
 *
 *  % java RandomWalk 20
 *  total steps = 300
 *
 *  % java RandomWalk 50
 *  total steps = 2630
 *
 *  Simulates a 2D random walk and plots the trajectory.
 *
 *  Remarks: works best if N is a divisor of 600.
 *
 *************************************************************************/

public class NsRProcess { 
  

    public static void main(String[] args) {
      OneDimensionalWalkApp walker = new OneDimensionalWalkApp();
      walker.setStepsPerDisplay(100);
      walker.getMainFrame();
      
    }

}

