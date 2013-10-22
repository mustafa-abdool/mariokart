//Using series, generate strips
//number of strips you want to add to 200 (or whatever the view hieght is)
//total height (strip_no*scalefactor) you want to add to 400 (or whatever the height of the screen is)
//BASICALLY....simple two variables statistics 


import java.util.*;


class GenTest{
	static int[] strips;
	static int[] scale_factor;
	
	static int scale_factor_change = 1; //rate of the change for the scale factor
	static int strip_change= 20; //rate of change for the scale factor
	
	static int range = 4; //length of the arraylist
	
	static int strip_sum=0;
	static int height_sum=0;
	
	public static void main(String[] args){
		scale_factor= new int[range];
		strips = new int[range];
		int count=0;
		
		for(int i=0;i<range;i++){
			scale_factor[i] = range-count;
			count++;
		}
		
		System.out.println(Arrays.toString(scale_factor));
		
		count=1;
		for (int j=0;j<range;j++){
			strips[j] = strip_change*count;
			count++;
		}
		System.out.println(Arrays.toString(strips));
		
		
		for (int a=0;a<range;a++){
			strip_sum+=strips[a];
			height_sum+=strips[a]*scale_factor[a];
			System.out.println("The scale factor of "+scale_factor[a]+" uses "+strips[a]+" many strips");
		}
		
		
		System.out.println("The total strips used are: "+strip_sum);
		System.out.println("The total height of all the strips are "+height_sum);
		
	}
}