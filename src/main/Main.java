package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application implements EventHandler<KeyEvent> {
	
	public static String[] cols = new String[] {"Constructive", "Rebuttal", "Summary"};
	public static ObservableList<TextArea>[][] lists;
	public static CustomListView<TextArea>[][] views;
	
	public static final int HEIGHT = 1280 / 2, WIDTH = 720 / 3;
	
    public static void main(String[] args) {
        launch(args);
    }
    
    public StackPane actualRoot;
    public GridPane root;
    public Pane linePane;
    public Stage primaryStage;
    public FileChooser fileChooser;
    
    @Override
    public void start(Stage primaryStage) {
    	this.primaryStage = primaryStage;
    	
    	fileChooser = new FileChooser();
		fileChooser.setTitle("Select File Name");
    	
    	System.out.println("Start");
        primaryStage.setTitle("PF Flow By Kenny Ge");
        
        root = new GridPane();
        
        for(int i = 0; i < 4; i++) {
        	ColumnConstraints cc = new ColumnConstraints(1280 / 3);
        	root.getColumnConstraints().add(cc);
        }
        
        lists = new ObservableList[2][cols.length];
        views = new CustomListView[2][cols.length];
        System.out.println("HI");
        
        for(int i = 0; i < cols.length; i++) {
        	createColumns(cols[i], i, 0, 720 / 2);
        	createColumns(cols[i], i, 1, 720 / 2);
        }
        System.out.println("Columns done");
        
        root.setOnKeyPressed(this);
        
        actualRoot = new StackPane();
        
        actualRoot.getChildren().add(root);
        root.setPrefHeight(HEIGHT);
        root.setPrefWidth(WIDTH);
        root.setPickOnBounds(false);
        
        linePane = new Pane();
        linePane.prefWidth(WIDTH);
        linePane.prefHeight(HEIGHT);
        setMouse(linePane);
        linePane.setMouseTransparent(true);
        actualRoot.getChildren().add(linePane);
        //linePane.setStyle("-fx-background-color:red");
        
        actualRoot.setAlignment(Pos.CENTER_LEFT);
        
        primaryStage.setScene(new Scene(actualRoot, 1280, 720));
        primaryStage.show();
        System.out.println("Show stage");
    }
    
    public double srcX, srcY;
    
    public void handleMouse(MouseEvent e) {
    	//System.out.println("release");
    	//System.out.println("RELEASED");
    	double destX = e.getSceneX();
    	double destY = e.getSceneY();
    	
    	//System.out.println(srcX + " " + srcY + " " + destX + " " + destY);
    	Line l = new Line(/*srcX, srcY, destX, destY*/);
    	l.setStrokeWidth(10);
    	l.setStartX(srcX);
    	l.setEndX(destX);
    	l.setStartY(srcY);
    	l.setEndY(destY);
    	
    	l.setOnMouseClicked(e2->{
    		actualRoot.getChildren().remove(l);
    	});
    	
    	linePane.getChildren().add(l);
    }
    
    public void click(MouseEvent e) {
    	srcX = e.getSceneX();
    	srcY = e.getSceneY();
    	System.out.println("Click");
    }
    
    public void setMouse(Node n) {
		n.setOnMousePressed(e->click(e));
		n.setOnMouseReleased(e->handleMouse(e));
    }
    
    public void createColumns(String title, int index, int other, int height) {
    	lists[other][index] = FXCollections.observableArrayList(createNewTextArea());
    	
    	CustomListView<TextArea> flows = new CustomListView<>(lists[other][index]);
    	flows.side = other;
    	flows.index = index;
    	
    	flows.setPrefHeight(height);
    	
    	views[other][index] = flows;
    	
    	flows.setOnKeyPressed((e)->{
    		if(e.isControlDown() && e.getCode() == KeyCode.ENTER) {
    			lists[flows.side][flows.index].add(createNewTextArea());
    		}
    	});
    	
    	if(other == 0)
    		root.add(new Label(title), index, other);
    	root.add(flows, index, other + 1);
    }
    
    public TextArea createNewTextArea() {
		TextArea html = new TextArea();
		html.setWrapText(true);
		html.setPrefWidth(1280 / 3 - 30);
		
		//hideTextAreaToolbars(html);
		  html.textProperty().addListener((observable, oldValue, newValue)->{
			      Text text = new Text();
			      text.setWrappingWidth(html.getWidth());
			      text.setText((String)newValue);
			      //System.out.println(text.getLayoutBounds().getHeight());
			      html.setPrefHeight(text.getLayoutBounds().getHeight() * 1.1f);
			    });
		return html;
    }
    
/*    public static void hideTextAreaToolbars(final TextArea editor)
    {
        editor.setVisible(false);
        Platform.runLater(new Runnable()
        {
            @Override
            public void run()
            {
                Node[] nodes = editor.lookupAll(".tool-bar").toArray(new Node[0]);
                for(Node node : nodes)
                {
                    node.setVisible(false);
                    node.setManaged(false);
                }
                editor.setVisible(true);
            }
        });
    }*/
    
    public static class CustomListView<E> extends ListView<E> {
    	public int side;
    	public int index;
    	public CustomListView(ObservableList<E> es) {
    		super(es);
    	}
    }
    
    public static final int UNIQUE_CODE = 123102312;
    
	@Override
	public void handle(KeyEvent event) {
		if(event.isControlDown() && event.getCode() == KeyCode.D) {
			linePane.setMouseTransparent(false);
			//actualRoot.getChildren().add(linePane);
		}
		if(event.isControlDown() && event.getCode() == KeyCode.F) {
			//actualRoot.getChildren().remove(linePane);
			linePane.setMouseTransparent(true);
		}
		if(event.isControlDown() && event.getCode() == KeyCode.S){
			File f = fileChooser.showSaveDialog(primaryStage);
			
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				for(int j = 0; j < 2; j++) {
					for(int i = 0; i < cols.length; i++) {
						bw.append(lists[j][i].size() + "\n");
						for(int k = 0; k < lists[j][i].size(); k++) {
							bw.append(lists[j][i].get(k).getText() + "\n");
						}
					}
				}
				bw.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
		
		if(event.isControlDown() && event.getCode() == KeyCode.O) {
			File f = fileChooser.showOpenDialog(primaryStage);
			
			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				for(int j = 0; j < 2; j++) {
					for(int i = 0; i < cols.length; i++) {
						int n = Integer.parseInt(br.readLine());
						List<TextArea> t = new ArrayList<>(n);
						for(int k = 0; k < n; k++) {
							TextArea ta = createNewTextArea();
							ta.setText(br.readLine());
							Text text = new Text();
							text.setWrappingWidth(WIDTH);
							text.setText((String)ta.getText());
							//System.out.println(text.getLayoutBounds().getHeight());
							ta.setPrefHeight(text.getLayoutBounds().getHeight());
							t.add(ta);
						}
						lists[j][i].setAll(t);
					}
				}
				br.close();
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}