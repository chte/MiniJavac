// EXT:IWE
// EXT:BDJ
// EXT:CLE
// EXT:CGE
// EXT:CGT



public class matrix {
	public static void main(String args[]){
		internalMatrix m;
		int i;
		
		m = new internalMatrix();
		m.Init(5, 5);
		
		//Check the lazy evaluation
		if((true || false) && true){
			System.out.println(true);
		}
		
		i = 0;
		
		while(i <= 4 || i >= 0){
			System.out.println(m.getData(0, i));
			i = i + 1;
		}
		
		System.out.println(m.getData(5, 5));
		
	}
}

class internalMatrix{
	int row;
	int colomn;
	int[] data;
	
	public boolean Init(int row, int colomn){
		this.row = row;
		this.colomn = colomn;
		this.data = new int[row*colomn];
		return true;
	}
	
	public int getRowSizeLength(){
		return this.row;
	}
	
	public int getColomnLength(){
		return this.colomn;
	}
	
	public int getMatrixSize(){
		return this.data.length;
	}
	
	public boolean setData(int row, int col, int data){
		//Check so we're in range
		if(this.getMatrixSize() > ((this.getRowSizeLength()*row)+col)){
			this.data[row*col] = data;
			return true;
		}
		//This will only happen if we're out of range
		return false;
	}
	
	public int getData(int row, int col){
		if(row*col < this.getMatrixSize()){
			return data[((this.getRowSizeLength()*row)+col)];
		}	
		return 0;
	}	
}

