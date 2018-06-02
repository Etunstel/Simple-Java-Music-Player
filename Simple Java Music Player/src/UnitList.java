import java.util.ArrayList;


/*
 * Represents playlists immediately before playing, will be constructed in order based on the list
 * of units in the Playlist
 */
public class UnitList {

	
	private Unit head;
	private Unit tail;
	private int length;
	
	public UnitList() {
		head = null;
		tail = null;
		length = 0;
	}
	
	public void addSong(Song s) {
		SingleSongUnit u = new SingleSongUnit(s);
		addUnit(u);
	}
	
	public void addUnit(Unit u) {
		if(length == 0) {
			head = tail = head.next = head.prev = tail.next = tail.prev = u;
		} else {
			tail.next = u;
			u.prev = tail;
			tail = u;
			head.prev = u;
		}
		
		length++;
	}
	
	
	public void removeUnit(Unit u) {
		
		Unit first = head;
		Unit curr, prev = null;
		curr = head;
		while (!curr.equals(u)) {
			prev = curr;
			curr = curr.next;
			if(curr.equals(first)) {
				return; // looped back to the beginning of list without finding u
			}
		}
		
		if(curr == tail) {
			tail = curr.next;
		}
		
		prev.next = curr.next;
		curr.next.prev = prev;
		length--;
			
		return;
	}
	
	
}
