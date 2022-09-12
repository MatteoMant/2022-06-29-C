package it.polito.tdp.itunes.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	private ItunesDAO dao;
	private Graph<Album, DefaultWeightedEdge> grafo;
	private Map<Album, Integer> albumPrezzo;
	
	public Model() {
		dao = new ItunesDAO();
		albumPrezzo = new HashMap<>();
	}
	
	public void creaGrafo(int prezzo) {
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		// Aggiunta dei vertici
		Graphs.addAllVertices(this.grafo, dao.getAllAlbumsWithPrice(prezzo));
		
		// Aggiunta degli archi
		for (Album a : this.grafo.vertexSet()) {
			int price = dao.calcolaPrezzoAlbum(a);
			albumPrezzo.put(a, price);
		}
		
		for (Album a1 : this.grafo.vertexSet()) {
			for (Album a2 : this.grafo.vertexSet()) {
				if (albumPrezzo.get(a1) != albumPrezzo.get(a2)) {
					if (this.grafo.getEdge(a1, a2) == null) {
						Graphs.addEdge(this.grafo, a1, a2, Math.abs(albumPrezzo.get(a1) - albumPrezzo.get(a2)));
					} else {
						// non faccio nulla perchè l'album esiste già
					}
				}
			}
		}
	}
	
	public List<AlbumBilancio> getAdiacenti(Album a1){
		List<AlbumBilancio> result = new LinkedList<>();
		
		for (Album a : Graphs.neighborListOf(this.grafo, a1)) {
			double bilancio = calcolaBilancio(a);
			result.add(new AlbumBilancio(a, bilancio));
		}
		Collections.sort(result);
		return result;
	}
	
	private double calcolaBilancio(Album a) {
		double bilancio = 0.0;
		
		for (DefaultWeightedEdge edge : this.grafo.edgesOf(a)) {
			bilancio += this.grafo.getEdgeWeight(edge);
		}
		bilancio = bilancio / this.grafo.degreeOf(a);
		return bilancio;
	}

	public List<Album> getAllAlbums(){
		List<Album> albums = new LinkedList<>(this.grafo.vertexSet());
		Collections.sort(albums);
		return albums;
	}
	
	public int getNumVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNumArchi() {
		return this.grafo.edgeSet().size();
	}
}
