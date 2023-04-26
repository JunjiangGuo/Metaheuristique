package jobshop.solvers;

import jobshop.Instance;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Schedule;
import jobshop.solvers.neighborhood.Neighborhood;
import jobshop.solvers.neighborhood.Nowicki;
import jobshop.solvers.BasicSolver;

import java.util.List;
import java.util.Optional;

/** An empty shell to implement a descent solver. */
public class DescentSolver implements Solver {

    final Neighborhood neighborhood;
    final Solver baseSolver;

    /** Creates a new descent solver with a given neighborhood and a solver for the initial solution.
     *
     * @param neighborhood Neighborhood object that should be used to generates neighbor solutions to the current candidate.
     * @param baseSolver A solver to provide the initial solution.
     */
    public DescentSolver(Neighborhood neighborhood, Solver baseSolver) {
        this.neighborhood = neighborhood;
        this.baseSolver = baseSolver;
    }

    @Override
    public Optional<Schedule> solve(Instance instance, long deadline) {
        Optional<Schedule> sch = baseSolver.solve(instance,deadline);
        long startTime = System.currentTimeMillis();
        ResourceOrder sol = new ResourceOrder(sch.get());
        List<ResourceOrder> neighbors = neighborhood.generateNeighbors(sol);
        while ((System.currentTimeMillis() - startTime < deadline)&&(neighbors!=null)){

            int makespanmin = 99999;
            ResourceOrder best = new ResourceOrder(instance);
            for (ResourceOrder neighbor : neighbors){
                if(neighbor.toSchedule().get().makespan()<=makespanmin){
                    best = neighbor.copy();
                    makespanmin = neighbor.toSchedule().get().makespan();
                }
            }
            if(sol.toSchedule().get().makespan()>makespanmin){
                sol = best.copy();
            }
        }

        return sol.toSchedule();
    }

}
