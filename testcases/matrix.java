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

	public internalMatrix matrixmultiplication(internalMatrix a,internalMatrix b){
		int aRows;
		int aColomns;
		int bRows;
		int bColomns;
		int i;
		int j;
		int k;
		internalMatrix c;

		aRows = a.getRowSizeLength();
		aColomns = a.getColomnLength();
		bRows = b.getRowSizeLength();
		bColomns = b.getColomnLength();

		if(aColomns != bRows){
			c = new internalMatrix();
			c.Init(0, 0);
			return c;
		}

		c = new internalMatrix();
		c.Init(aRows,bColomns);

		i = 0;
		j = 0;
		while(i < aRows){
			while(j < bColomns){
				c.setData(i,j,0);
				j = j + 1;
			}
			i = i + 1;
		}

		i = 0;
		j = 0;
		k = 0;

		while(i < aRows){
			while(j < bColomns){
				while(k < aColomns){
					c.setData(i,j,(a.getData(i,k)*b.getData(k,j)));
					k = k + 1;
				}
				j = j + 1;
			}
			i = i +1;
		}

		return c;
	}	
}

