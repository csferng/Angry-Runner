package tw.edu.ntu.csie.angryrunner;

public class Song {

	public int index;
	public String name;
	public String filePath;
	public boolean checked;

	public Song(String name) {
		this.name = name;
		checked = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String toString() {
		return name;
	}

	public void toggleChecked() {
		checked = !checked;
	}
	
}
