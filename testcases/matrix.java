// EXT:IWE
// EXT:BDJ
// EXT:CLE
// EXT:CGE
// EXT:CGT

public class matrix {
	public static void main(String args[]){
		internalMatrix m;
		int i;
		int j;
		internalMatrix n;
		internalMatrix o;
		
		m = new internalMatrix();
		n = new internalMatrix();
		m.Init(2,2);
		n.Init(2,2);
		
		//Check the lazy evaluation
		if((false || false) && true){
			System.out.println(true);
		}
		
		i = 0;
		j = 0;
		
		while(i <= m.getMatrixSize()){
			System.out.println(m.getData(i,j));
			i = i + 1;
		}

		i = 0;
		//Set the m and n matrix to only 2
		while(i < m.getColomnLength()){
			j = 0;
			while(j < m.getColomnLength()){
				m.setData(i,j,2);
				n.setData(i,j,2);
				j = j + 1;
			}
			i = i + 1;
		}
		
		o = m.matrixmultiplication(m,n);
		i = 0;
		
		while(i < o.getColomnLength()){
			j = 0;
			while(j < o.getRowLength()){
				System.out.println(o.getData(i,j));
				j = j + 1;
			}
			i = i + 1;
		}		
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
	
	public int getRowLength(){
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
		if(this.getMatrixSize() > ((this.getRowLength()*row)+col)){
			this.data[((this.getRowLength()*row)+col)] = data;
			return true;
		}
		//This will only happen if we're out of range
		return false;
	}
	
	public int getData(int row, int col){
		if(((this.getRowLength()*row)+col) < this.getMatrixSize()){
			return data[((this.getRowLength()*row)+col)];
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

		aRows = a.getRowLength();
		aColomns = a.getColomnLength();
		bRows = b.getRowLength();
		bColomns = b.getColomnLength();

		if(aColomns != bRows){
			c = new internalMatrix();
			c.Init(0, 0);
			return c;
		}

		c = new internalMatrix();
		c.Init(aRows,bColomns);

		i = 0;
		while(i < aRows){
			j = 0;
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
			j = 0;
			while(j < bColomns){
				k = 0;
				while(k < aColomns){
					c.setData(i,j, c.getData(i,j)+(a.getData(i,k)*b.getData(k,j)));
					k = k + 1;
				}
				j = j + 1;
			}
			i = i + 1;
		}

		return c;
	}	
}

