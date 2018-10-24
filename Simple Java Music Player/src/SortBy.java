/*  MultiSongUnits will always be lexicographically greater than SingleSong Units.
 *  MultiSongUnits will be ordered based on the [Title, Artist, Album, or Duration]
 *  of the first songs in their list of songs.
    Unused as of now, since TableViews reorder their backing ObservableLists automatically
*/

import java.util.Comparator;

public enum SortBy implements Comparator<Unit>{
	
	 TITLE_ASCENDING {
	        @Override
	        public final int compare(final Unit u1, final Unit u2) {
	            UnitType u1Type = u1.getUnitType();
	            UnitType u2Type = u2.getUnitType();
	            
	            if (u1Type == u2Type) {
	            	if(u1Type == UnitType.SINGLE) {
	            		SingleSongUnit m1 = (SingleSongUnit) u1;
	            		SingleSongUnit m2 = (SingleSongUnit) u2;
	            		
	            		Song s1 = m1.getSong();
	            		Song s2 = m2.getSong();
	            		
	            		return s1.getTitle().compareTo(s2.getTitle());
	            		
	            	} else { // both Multi
	            		MultiSongUnit m1 = (MultiSongUnit) u1;
	            		MultiSongUnit m2 = (MultiSongUnit) u2;
	            		
	            		Song s1 = m1.getSongs().get(0);
	            		Song s2 = m2.getSongs().get(0);
	            		
	            		return s1.getTitle().compareTo(s2.getTitle());
	            	}
	            } else {
	            	if(u1Type == UnitType.SINGLE) {
	            		return -1;
	            	} else {
	            		return 1;
	            	}
	            }
	        }
	    },
	  TITLE_DESCENDING{
	        @Override
	        public final int compare(final Unit u1, final Unit u2) {
	            return -1 * TITLE_ASCENDING.compare(u1, u2);
	        }
	    },
	   ARTIST_ASCENDING {
	        @Override
	        public final int compare(final Unit u1, final Unit u2) {
	            UnitType u1Type = u1.getUnitType();
	            UnitType u2Type = u2.getUnitType();
	            
	            if (u1Type == u2Type) {
	            	if(u1Type == UnitType.SINGLE) {
	            		SingleSongUnit m1 = (SingleSongUnit) u1;
	            		SingleSongUnit m2 = (SingleSongUnit) u2;
	            		
	            		Song s1 = m1.getSong();
	            		Song s2 = m2.getSong();
	            		
	            		return s1.getArtist().compareTo(s2.getArtist());
	            		
	            	} else { 
	            		MultiSongUnit m1 = (MultiSongUnit) u1;
	            		MultiSongUnit m2 = (MultiSongUnit) u2;
	            		
	            		Song s1 = m1.getSongs().get(0);
	            		Song s2 = m2.getSongs().get(0);
	            		
	            		return s1.getArtist().compareTo(s2.getArtist());
	            	}
	            } else {
	            	if(u1Type == UnitType.SINGLE) {
	            		return -1;
	            	} else {
	            		return 1;
	            	}
	            }
	        }
	    },
	  ARTIST_DESCENDING{
	        @Override
	        public final int compare(final Unit u1, final Unit u2) {
	        	return -1 * ARTIST_ASCENDING.compare(u1, u2);
	        }
	    },
	   ALBUM_ASCENDING {
	        @Override
	        public final int compare(final Unit u1, final Unit u2) {
	            UnitType u1Type = u1.getUnitType();
	            UnitType u2Type = u2.getUnitType();
	            
	            if (u1Type == u2Type) {
	            	if(u1Type == UnitType.SINGLE) {
	            		SingleSongUnit m1 = (SingleSongUnit) u1;
	            		SingleSongUnit m2 = (SingleSongUnit) u2;
	            		
	            		Song s1 = m1.getSong();
	            		Song s2 = m2.getSong();
	            		
	            		return s1.getAlbum().compareTo(s2.getAlbum());
	            		
	            	} else { 
	            		MultiSongUnit m1 = (MultiSongUnit) u1;
	            		MultiSongUnit m2 = (MultiSongUnit) u2;
	            		
	            		Song s1 = m1.getSongs().get(0);
	            		Song s2 = m2.getSongs().get(0);
	            		
	            		return s1.getAlbum().compareTo(s2.getAlbum());
	            	}
	            } else {
	            	if(u1Type == UnitType.SINGLE) {
	            		return -1;
	            	} else {
	            		return 1;
	            	}
	            }
	        }
	    }, 
	   ALBUM_DESCENDING {
	        @Override
	        public final int compare(final Unit u1, final Unit u2) {
	        	return -1 * ALBUM_ASCENDING.compare(u1, u2);
	        }
	    },
	    DURATION_ASCENDING {
	        @Override
	        public final int compare(final Unit u1, final Unit u2) {
	            UnitType u1Type = u1.getUnitType();
	            UnitType u2Type = u2.getUnitType();
	            
	            if (u1Type == u2Type) {
	            	if(u1Type == UnitType.SINGLE) {
	            		SingleSongUnit m1 = (SingleSongUnit) u1;
	            		SingleSongUnit m2 = (SingleSongUnit) u2;
	            		
	            		Song s1 = m1.getSong();
	            		Song s2 = m2.getSong();
	            		
	            		return s1.getDuration().compareTo(s2.getDuration());
	            		
	            	} else { 
	            		MultiSongUnit m1 = (MultiSongUnit) u1;
	            		MultiSongUnit m2 = (MultiSongUnit) u2;
	            		
	            		Song s1 = m1.getSongs().get(0);
	            		Song s2 = m2.getSongs().get(0);
	            		
	            		return s1.getDuration().compareTo(s2.getDuration());
	            	}
	            } else {
	            	if(u1Type == UnitType.SINGLE) {
	            		return -1;
	            	} else {
	            		return 1;
	            	}
	            }
	        }
	    }, 
	    DURATION_DESCENDING {
	        @Override
	        public final int compare(final Unit u1, final Unit u2) {
	        	return -1 * DURATION_ASCENDING.compare(u1, u2);
	        }
	    };
}
