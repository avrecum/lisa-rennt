public class Static {
    static double digits = 3.0;
    static double tolerance = Math.pow(10.0, digits);
    public static double round(double d){
        return Math.floor(d* tolerance)/ tolerance;
    }
}
