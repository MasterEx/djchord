/*
 * Ntanasis Periklis - A.M.:3070130
 * Chatzipetros Mike - A.M.:3070175
 *
 * den katafera na kanw override/overload (opws legetai :P) tous telestes :(
 */

package basic;

class djint
{
	private byte[] m;

	/*
	 *empty constructor
	*/
	public djint()
	{
		m = new byte[160];
	}

	/*
	 *byte constructor
	*/
	public djint(byte[] n)
	{
		m=n;
	}

	/*
	 *epistrefei tin byte timi
	*/
	public byte[] byteValue(){
		return this.m;
	}
	/*
	 *prosthesi
	*/
	public djint add(djint n)
	{
		byte[] j = n.byteValue();
		for (int i=0;i<160;i++)
		{
			this.m[i]+=j[i];
		}
		return this;
	}
	/*
	 *afairesi
	*/
	public djint sub(djint n)
	{
		byte[] j = n.byteValue();
		for (int i=0;i<160;i++)
		{
			this.m[i]-=j[i];
		}
		return this;
	}
	
	
}
