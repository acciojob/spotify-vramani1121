package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {

        boolean foundartist = false;
        Artist artist = new Artist(artistName);;
        for(int i=0; i<artists.size(); i++){
            if(artistName == artists.get(i).getName()){
                foundartist = true;

            }
        }

        if(!foundartist){

            artists.add(artist);
        }

        Album album = new Album(title);
        albums.add(album);
        List<Album> getedAlbum = new ArrayList<>();
        if(artistAlbumMap.containsKey(artistName)){
            getedAlbum = artistAlbumMap.get(artistName);
            getedAlbum.add(album);
        }
        else{
            artistAlbumMap.put(artist,new ArrayList<>((Collection) album));
        }
        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{

        Song song = new Song(title,length);
        if(!albumSongMap.containsKey(albumName)){
            throw new Exception("Album does not exist");
        }
        else{
            List <Song> list = albumSongMap.get(albumName);

            list.add(song);
        }
        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        //Create a playlist with given title and add all songs having the given length in the database to that playlist
        //The creater of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception
        Playlist playlist = new Playlist(title);
        List<Song> list = new ArrayList<>();
        for(Song song:songs){
            if(song.getLength() == length){
                list.add(song);
            }
        }
        playlistSongMap.put(playlist,list);
        if(!playlistListenerMap.containsKey(playlist)){
            throw new Exception("User does not exist");
        }
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        //Create a playlist with given title and add all songs having the given titles in the database to that playlist
        //The creater of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception
        Playlist playlist = new Playlist(title);
        List<Song> list = new ArrayList<>();
        for(Song song:songs){
            if(song.getTitle() == title){
                list.add(song);
            }
        }
        playlistSongMap.put(playlist,list);
        if(!playlistListenerMap.containsKey(playlist)){
            throw new Exception("User does not exist");
        }
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {

        List <User> list = new ArrayList<>();
        Playlist playlist = new Playlist(playlistTitle);
        if(playlistListenerMap.containsKey(playlist)){
            list = playlistListenerMap.get(playlist);
        }
        else {
            throw new Exception("Playlist does not exist");
        }
        User user = new User();
        user.setMobile(mobile);

        list.add(user);

        if(!userPlaylistMap.containsKey(user)){
            throw new Exception("User does not exist");
        }

        if (creatorPlaylistMap.containsKey(user)){
            return playlist;
        }
        return playlist;

    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        //The user likes the given song. The corresponding artist of the song gets auto-liked
        //A song can be liked by a user only once. If a user tried to like a song multiple times, do nothing
        //However, an artist can indirectly have multiple likes from a user, if the user has liked multiple songs of that artist.
        //If the user does not exist, throw "User does not exist" exception
        //If the song does not exist, throw "Song does not exist" exception
        //Return the song after updating
        User user = new User(mobile);
        Song song = new Song(songTitle);
        if(!songLikeMap.containsKey(song)){
            songLikeMap.put(song,new ArrayList<>());
            List <User> list = songLikeMap.get(song);
            list.add(user);

        }else {
            List <User> list = songLikeMap.get(song);
            list.add(user);
        }
        return song;

    }

    public String mostPopularArtist() {
        int max = Integer.MIN_VALUE;
        String songTitle="";
        Song song = null;
        for (Map.Entry<Song, List<User>> entry : songLikeMap.entrySet()){
            if(entry.getValue().size() > max){
                songTitle = entry.getKey().getTitle();
                song = entry.getKey();
                max = entry.getValue().size();
            }
        }
       return songTitle;

    }

    public String mostPopularSong() {
        int max = Integer.MIN_VALUE;
        String songTitle="";

        for (Map.Entry<Song, List<User>> entry : songLikeMap.entrySet()){
            if(entry.getValue().size() > max){
                songTitle = entry.getKey().getTitle();

                max = entry.getValue().size();
            }
        }
        return songTitle;
    }
}
