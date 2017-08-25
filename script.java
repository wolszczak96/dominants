import java.util.*;

class Main{
	public static void main(String[] args){
		Note note1 = new Note(-1, 0);
		List<Note> dominant = Build.dominant(note1);
		List<Note> solution = Build.dominant_solution(dominant, 1);
		String name = Read.notes(dominant);
		String read = Read.dominant_solution_notes(solution);
		for(int i = 0; i < 4; i++){
			if(i != 0){
				dominant = Build.inversion(dominant, 1);
				solution = Build.inversion(solution, 1);
				name = Read.notes(dominant);
				read = Read.dominant_solution_notes(solution);
			}
			System.out.println(i + ". inversion:");
			System.out.println("Dominant: " + name);
			System.out.println("Solution: " + read);
		}
	}
}

class Color{
	public String red(String text){
		return "\033[1m\033[91m" + text + "\033[0m";
	}

	public String green(String text){
		return "\033[1m\033[92m" + text + "\033[0m";
	}
}

class Count{
	public static String alteration(int shf){
		String modifier = "";
		if(shf >= 0){
			for(int i = 0; i < shf; i++){
				modifier = modifier + "#";
			}
		}
		else{
			for(int i = 0; i < Math.abs(shf); i++){
				modifier = modifier + "b";
			}
		}
		return modifier;
	}

	public static int halfstep(int index, int shf){
		List<Integer> halfsteps = Arrays.asList(0, 2, 3, 5, 7, 8, 10);
		int octave = 0;
		if(index < 0){
			octave = (index - 6)/7;
		}
		else{
			octave = index/7;
		}
		int cursor = index - octave*7;
		int halfstep = halfsteps.get(cursor) + octave*12 + shf;
		return halfstep;
	}
}

class Note{
	private static final List<String> notes = Arrays.asList("A", "B", "C", "D", "E", "F", "G");
	private String name;
	private int index;
	private int shf;
	public Note(int index, int shf){
		int octave = 0;
		if(index < 0){
			octave = (index - 6)/7;
		}
		else{
			octave = index/7;
		}
		int cursor = index - octave*7;
		int halfstep = Count.halfstep(index, shf);
		this.name = notes.get(cursor) + Count.alteration(shf);
		this.index = index;
		this.shf = shf;
	}

	public String getName(){
		return this.name;
	}

	public int getIndex(){
		return this.index;
	}

	public int getShf(){
		return this.shf;
	}
}

class Build{
	private static final List<Integer> intervals = Arrays.asList(0, 2, 4, 5, 7, 9, 10);
	public static Note interval(String interval, int direction, Note note){
		int halfstep = Count.halfstep(note.getIndex(), note.getShf());
		int jump = 0;
		String digits = "";
		for(int i = 0; i < interval.length(); i++){
			if(Character.isDigit(interval.charAt(i))){
				digits = digits + interval.substring(i, i+1);
			}
			else if(interval.substring(i, i+1).equals(">")){
				jump--;
			}
			else if(interval.substring(i, i+1).equals("<")){
				jump++;
			}
		}
		int step = Integer.parseInt(digits) - 1;
		int octave = step/7;
		int index = step%7;
		jump = jump + intervals.get(index) + octave*12;
		if(direction == 0){
			jump = -jump;
			step = -step;
		}
		int shf = (halfstep + jump) - Count.halfstep(note.getIndex() + step, 0);
		return new Note(note.getIndex() + step, shf);
	}

	public static List<Note> dominant(Note note){
		Note note1 = note;
		Note note3 = Build.interval("3", 1, note1);
		Note note5 = Build.interval("3>", 1, note3);
		Note note7 = Build.interval("3>", 1, note5);
		List<Note> notes = new LinkedList<>();
		notes.add(note1);
		notes.add(note3);
		notes.add(note5);
		notes.add(note7);
		return notes;
	}

	public static List<Note> dominant_solution(List<Note> dominant, int mode){
		Note note1 = dominant.get(0);
		Note note2 = Build.interval("2>", 1, dominant.get(1));
		Note note3 = Build.interval("2", 0, dominant.get(2));
		Note note4;
		if(mode == 0){
			note4 = Build.interval("2", 0, dominant.get(3));
		}
		else{
			note4 = Build.interval("2>", 0, dominant.get(3));
		}
		List<Note> notes = new LinkedList<>();
		notes.add(note1);
		notes.add(note2);
		notes.add(note3);
		notes.add(note4);
		return notes;
	}

	public static List<Note> inversion(List<Note> chord, int inv){
		inv = inv%chord.size();
		if(inv < 0){
			inv = chord.size() + inv;
		}
		List<Note> notes = new LinkedList<>();
		for(int i = 0; i < chord.size()-inv; i++){
			notes.add(chord.get(i + inv));
		}
		for(int i = 0; i < inv; i++){
		notes.add(chord.get(i));
		}
		return notes;
	}
}

class Read{
	public static String notes(List<Note> chord){
		String result = "";
		for(int i = 0; i < chord.size() - 1; i++){
			result = result + chord.get(i).getName() + " ";
		}
		result = result + chord.get(chord.size() - 1).getName();
		return result;
	}

	public static String dominant_solution_notes(List<Note> solution){
		int cursor1 = solution.size() - 1;
		int cursor2 = cursor1 - 1;
		List<Note> notes = new LinkedList<>();
		while(cursor1 >= 0 && cursor2 >= 0){
			if(solution.get(cursor1).getName().equals(solution.get(cursor2).getName())){
				notes.add(solution.get(cursor1));
				cursor1 = cursor1 - 2;
				while(cursor1 >= 0){
					notes.add(solution.get(cursor1));
					cursor1--;
				}
				notes.add(solution.get(cursor2));
			}
			else{
				notes.add(solution.get(cursor1));
				cursor1--;
				cursor2--;
				if(cursor1 == 0){
					notes.add(solution.get(cursor1));
				}
			}
		}
		String result = Read.notes(notes);
		return result;
	}
}

