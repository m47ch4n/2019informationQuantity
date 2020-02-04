package s4.B193362; // Please modify to s4.Bnnnnnn, where nnnnnn is your student ID. 

import java.lang.*;
import s4.specification.*;

/* What is imported from s4.specification
package s4.specification;
public interface InformationEstimatorInterface{
    void setTarget(byte target[]); // set the data for computing the information quantities
    void setSpace(byte space[]); // set data for sample space to computer probability
    double estimation(); // It returns 0.0 when the target is not set or Target's length is zero;
// It returns Double.MAX_VALUE, when the true value is infinite, or space is not set.
// The behavior is undefined, if the true value is finete but larger than Double.MAX_VALUE.
// Note that this happens only when the space is unreasonably large. We will encounter other problem anyway.
// Otherwise, estimation of information quantity, 
}                        
*/

public class InformationEstimator implements InformationEstimatorInterface {
	// Code to tet, *warning: This code condtains intentional problem*
	byte[] myTarget; // data to compute its information quantity
	byte[] mySpace; // Sample space to compute the probability
	double[] memo;
	FrequencerInterface myFrequencer; // Object for counting frequency
	boolean targetReady = false;
	boolean spaceReady = false;

	byte[] subBytes(byte[] x, int start, int end) {
		// corresponding to substring of String for byte[] ,
		// It is not implement in class library because internal structure of byte[]
		// requires copy.
		byte[] result = new byte[end - start];
		for (int i = 0; i < end - start; i++) {
			result[i] = x[start + i];
		}
		;
		return result;
	}

	public void setTarget(byte[] target) {
		myTarget = target;
		if (myTarget.length > 0)
				targetReady = true;
	}

	public void setSpace(byte[] space) {
		myFrequencer = new Frequencer();
		mySpace = space;
		if (mySpace.length > 0)
				spaceReady = true;
		myFrequencer.setSpace(space);
	}

	private double f(byte[] str) {
		myFrequencer.setTarget(str);
		int freq = myFrequencer.frequency();
		return -Math.log10((double) freq / (double) mySpace.length) / Math.log10((double) 2.0);
	}

	// IQ: information quantity for a count, -log2(count/sizeof(space))
	private double iq(byte[] str) {
		if(memo[str.length-1] != Double.MAX_VALUE){
			return memo[str.length-1];
		}
		double value = f(str);
		for (int i = 1; i < str.length; i ++) {
			// "abc"
			// s1 = subBytes(str, 0, 1) => "a"
			// s2 = subBytes(str, 1, 3) => "bc"
			byte[] s1 = subBytes(str, 0, i);
			byte[] s2 = subBytes(str, i, str.length);

			value = Math.min(value, iq(s1)+f(s2));
		}
		
		memo[str.length-1] = value;
		return value;
	}


	public double estimation() {
		if (targetReady == false)
				return 0.0;
		if (spaceReady == false)
				return Double.MAX_VALUE;

		memo = new double[myTarget.length];
		for (int i = 0; i < memo.length; i ++) {
			memo[i] = Double.MAX_VALUE;
		}

		double result = iq(myTarget);
		return result != Double.POSITIVE_INFINITY ? result : Double.MAX_VALUE;
	}

	public static void main(String[] args) {
		InformationEstimator myObject;
		double value;
		myObject = new InformationEstimator();
		myObject.setSpace("3210321001230123".getBytes());
		myObject.setTarget("0".getBytes());
		value = myObject.estimation();
		System.out.println(">0 " + value);
		myObject.setTarget("01".getBytes());
		value = myObject.estimation();
		System.out.println(">01 " + value);
		myObject.setTarget("0123".getBytes());
		value = myObject.estimation();
		System.out.println(">0123 " + value);
		myObject.setTarget("00".getBytes());
		value = myObject.estimation();
		System.out.println(">00 " + value);
	}
}
