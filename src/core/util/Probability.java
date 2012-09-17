package core.util;
import ec.util.MersenneTwisterFast;
/**
 * A utility class for various probability distributions.
 */
public class Probability {
	/**
	 * Returns time-to-wait as sampled under an exponential distribution with
	 * given mean.
	 */
	public static double getRandomWaitTime(double mean, MersenneTwisterFast rng){
		return -Math.log(1-rng.nextDouble())/mean;
	}
	public static double gaussianPdf(double mean, double sigma, double x){
		return (1/(Math.sqrt(2*Math.PI*Math.pow(sigma,2)))) * Math.exp(-Math.pow(x-mean,2)/(2*Math.pow(sigma,2)));
	}
}
