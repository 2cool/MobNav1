package mobnav.tracks;

public class PathNode{
	boolean correct=false;
	int i;	  //Index узла
	public double dist=0; //дистанция он начало узла до gp опущенного на прямую.
	public double dist_from_beg; //дистаниця от начала трека до узла
	public PathNode(final int i, final double dist_from_beg){
		this.i=i;
		this.dist_from_beg=dist_from_beg;
	}
}