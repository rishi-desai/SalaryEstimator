import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;

/**
 *  The class performs a simple linear regression
 *  on an set of data points. 
 *  That is, it fits a straight line y and x,
 *  (where y is the response variable, and x is the predictor variable,
 *  that minimizes the sum of squared residuals of the linear regression model.
 *  It also computes associated statistics, including the coefficient of
 *  determination R2 and the standard deviation of the
 *  estimates for the slope and y intercept.
 *  
 *  @author rishi desai
 *  
 */
public class SalaryEstimator {
	private static double[] yearsOfExperience = new double[30];
	private static double[] salary = new double[30];
	private double intercept = 0;
	private double slope = 0;
	private double r2 = 0;
	private double svar0 = 0;
	private double svar1 = 0;

	/**
	 * Takes the crv file, that stores the sample data of years of experience and correlating salary,
	 * and reads the values into two separate lists parsing the information into yearOfExperience and salary.
	 * Which can then be used to make a linear regression model.
	 */
	public static void salaryData() {
		// may need to be changes to represent where you store salaryData.csv
		String path = "/Users/rishi/Desktop/Projects/SalaryEstimator/resources/salaryData.csv";
		String line = "";
		int i = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			int iteration = 0;
			while((line = br.readLine()) != null) {
				if (iteration == 0) {
					iteration++;
					continue;
				}
				String[] values = line.split(",");
				if (i != yearsOfExperience.length) {
					yearsOfExperience[i] = Double.parseDouble(values[0]);
				}
				if (i != salary.length) {
					salary[i] = Double.parseDouble(values[1]);
				}
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Takes the values got from the data set in the method SalaryData and used them to find the means of each set:
	 * x and y. Then, taking the means it calculated the slope and y-intercept. Next, in order to confirm the
	 * reliability of our prediction the program finds the residual sum of the squares and the regression sum of the
	 * squares, which then can be used to find the coefficient of determination which is our reliability factor.
	 * Constructor method which finds all the values needed for the prediction.
	 *
	 * @param x the x-axis values
	 * @param y the y-axis values
	 */
	public SalaryEstimator(double[] x, double[] y) {
		if (x.length != y.length) {
			throw new IllegalArgumentException("array lengths are not equal");
		}
		int n = x.length;

		// first pass
		double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
		for (int i = 0; i < n; i++) {
			sumx  += x[i];
			sumx2 += x[i]*x[i];
			sumy  += y[i];
		}
		double xbar = sumx / n;
		double ybar = sumy / n;

		// second pass: compute summary statistics
		double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
		for (int i = 0; i < n; i++) {
			xxbar += (x[i] - xbar) * (x[i] - xbar);
			yybar += (y[i] - ybar) * (y[i] - ybar);
			xybar += (x[i] - xbar) * (y[i] - ybar);
		}
		slope  = xybar / xxbar;
		intercept = ybar - slope * xbar;

		// more statistical analysis
		double rss = 0.0;      // residual sum of squares
		double ssr = 0.0;      // regression sum of squares
		for (int i = 0; i < n; i++) {
			double fit = slope*x[i] + intercept;
			rss += (fit - y[i]) * (fit - y[i]);
			ssr += (fit - ybar) * (fit - ybar);
		}

		int degreesOfFreedom = n-2;
		r2    = ssr / yybar;
		double svar  = rss / degreesOfFreedom;
		svar1 = svar / xxbar;
		svar0 = svar/n + xbar*xbar*svar1;
	}

	/**
	 * Find the intercept of the best-fit line.
	 *
	 * @return the intercept
	 */
	public double intercept() {
		return intercept;
	}

	/**
	 *  Find the slope of the best-fit line.
	 *
	 * @return the slope
	 */
	public double slope() {
		return slope;
	}

	/**
	 * returns the coefficient of determination
	 * r2 is represented as a value between 0.0 and 1.0, where a value of 1.0 indicates a perfect fit,
	 * and is thus a highly reliable model for future forecasts, while a value of 0.0 would indicate that the model 
	 * fails to accurately model the data at all. 
	 */
	/**
	 * Finds the coefficient of determination r2 is represented as a value between 0.0 and 1.0,  where a value of 1.0
	 * indicates a perfect fit, and is thus a highly reliable model for future forecasts, while a value of 0.0 would
	 * indicate that the model fails to accurately model the data at all.
	 *
	 * @return teh coefficient of determination
	 */
	public double R2() {
		return r2;
	}

	/**
	 * Find the standard error of the estimate for the intercept.
	 *
	 * @return the standard error for the intercept
	 */
	public double interceptStdErr() {
		return Math.sqrt(svar0);
	}

	/**
	 * Find the standard error of the estimate for the slope.
	 *
	 * @return the standard error for the slope
	 */
	public double slopeStdErr() {
		return Math.sqrt(svar1);
	}

	/**
	 * Find the excepted response given the value of teh predictor variable.
	 *
	 * @param x the predictor variable
	 * @return the excepted response
	 */
	public double predict(double x) {
		return slope * x + intercept;
	}

	/**
	 * Find string representation of the linear regression model.
	 *
	 * @return string of the LRM
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(String.format("%.2f x + %.2f", slope(), intercept()));
		str.append("    (R^2 = " + String.format("%.3f", R2()) + ")");
		return str.toString();
	}


	/**
	 * Main method that runs the linear regression model.
	 *
	 * @param args the string array
	 */
	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);

		salaryData();

		SalaryEstimator model1 = new SalaryEstimator(yearsOfExperience, salary);

		System.out.println("Enter how many years of work experience you have.");
		double years = keyboard.nextDouble();


		double newSalaryPredict = model1.predict(years);


		System.out.println(model1.toString());
		System.out.println("The predicted salary of a person with " + years + " year(s) of experience is $"
				+ String.format("%.2f", newSalaryPredict));

		keyboard.close();

	}
}
