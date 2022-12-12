 package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.ui.Picture;

/**
 * Creación de videojuego TOWER DEFENSE
 * @author Ana Cecilia Flores García
 */
public class Main extends SimpleApplication {
    

    //Variable para el apuntador
    private final static Trigger TRIGGER_ROTATE = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private final static String MAPPING_ROTATE = "Rotate";
    
    private Geometry geomENE;//GEOMETRÍA DEL ENEMIGO así puede ser manipulada

    public boolean eliminado;//Funciona para saber si el jugador ha eliminado a algún ENEMIGO
    int numeroMuertes = 0; //Contabiliza el número de muertes del ENEMIGO
    
    float aceleradortpf = 10.5f; //Multiplica el tpf para aumentarlo
    
    int posicionX = -4; //Auxiliar para asignar el valor de -4 al eje X del ENEMIGO.
    int posicionY = 2;  //Auxiliar para asignar el valor de 2 al eje Y del ENEMIGO.
    
    
    
    //Creación de Quaternion para la ubicación de la cámara.
    public static final Quaternion PITCH090 = new Quaternion().fromAngleAxis(FastMath.PI, new Vector3f(0,0,0));

    //Crear el objeto para controlar las especificaciones
    public static void main(String[] args) {
        
        AppSettings settings = new AppSettings(true);
        settings.setTitle("TOWER DEFENSE");//Cambiamos el nombre de la ventana
        
        //Integramos una imagen personal a la pantalla de inicio
        settings.setSettingsDialogImage("Interface/torre.png");
        
        Main app = new Main();
        app.setSettings(settings);//Aplicamos las especificaciones a la app
        
        app.start(); //Iniciamos el videojuego        
    }
    
    /**
     * attachCenterMark crea un objeto geometry que servira de mira para apuntar 
     * diferentes objetos en el escenario ya que es una marca 2D, se debe adjuntar
     * a la interface 2D del usuario "guiNode" , este objeto es instanciado en cualquier
     * SimpleApplication
     */
    private void attachCenterMark(){
        //Creación del APUNTADOR
        Sphere bola= new Sphere(60,60,1);
        Geometry c = new Geometry("Bolita", bola);
        c.scale(2);
        c.setLocalTranslation(settings.getWidth()/2, settings.getHeight()/2, 0);
        Material matbola = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matbola.setColor("Color", ColorRGBA.White);
        c.setMaterial(matbola);
        
        guiNode.attachChild(c);//adjunta el puntero como NODO
    }

    @Override
    public void simpleInitApp() {
        
        //Extender el flyCam
        flyCam.setMoveSpeed(10f);
        //Cambiar la ubicación y rotación de la cámara para dar perspectiva
        //cam.setLocation(new Vector3f(0, 6, 16));
        cam.setLocation(new Vector3f(0, 6, -16)); //Esta la puse para por mientras del espacio
        cam.setRotation(PITCH090);
        
        //PARA EL PUNTERO
        inputManager.addMapping(MAPPING_ROTATE, TRIGGER_ROTATE);
        inputManager.addListener(analogListener, new String[]{MAPPING_ROTATE});
        
        
        //Creación del terreno
        Box suelo = new Box(10f,1f,25);
        Geometry geom = new Geometry("geom", suelo);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Textures/suelo.png"));
        geom.setMaterial(mat);
       
          
        //Creación del torree
        Box torre = new Box(5.5f,2,5f);
        Geometry geomTOR = new Geometry("geomTOR", torre);
        Material matTOR = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matTOR.setTexture("ColorMap", assetManager.loadTexture("Textures/castillo.jpg"));
        geomTOR.setMaterial(matTOR);
        geomTOR.move(0, 3, -20);
        
        //Creación del torree 2
        Box torre2 = new Box(4f,2,5f);
        Geometry geomTOR2 = new Geometry("geomTOR", torre2);
        Material matTOR2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matTOR2.setTexture("ColorMap", assetManager.loadTexture("Textures/aa.png"));
        geomTOR2.setMaterial(matTOR2);
        geomTOR2.move(0, 6, -20); 
        
        //Creación torreParada
        Box prueba = new Box(2.5f,5.5f,5f);
        Geometry pruebaG = new Geometry("geomPIL", prueba);
        Material pruebaM= new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        pruebaM.setTexture("ColorMap", assetManager.loadTexture("Textures/otro.png"));
        pruebaG.setMaterial(pruebaM);
        pruebaG.move(7, 6, -20);
        
        //Creación torreParada 1
        Box prueba1 = new Box(2.5f,5.5f,5f);
        Geometry pruebaG1 = new Geometry("geomPIL", prueba1);
        Material pruebaM1= new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        pruebaM1.setTexture("ColorMap", assetManager.loadTexture("Textures/otro.png"));
        pruebaG1.setMaterial(pruebaM1);
        pruebaG1.move(-7, 6, -20);
       
        //Creación del pilar 0
        Box pilar = new Box(0.4f,2.5f,0.3f);
        Geometry geomPIL = new Geometry("geomPIL", pilar);
        Material matPIL = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matPIL.setTexture("ColorMap", assetManager.loadTexture("Textures/torre.png"));
        geomPIL.setMaterial(matPIL);
        geomPIL.move(7.5f, 3, 12);
        
        //Creación del pilar 1
        Box pilar1 = new Box(0.4f,2.5f,0.3f);
        Geometry geomPIL1 = new Geometry("geomPIL1", pilar1);
        Material matPIL1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matPIL1.setTexture("ColorMap", assetManager.loadTexture("Textures/torre.png"));
        geomPIL1.setMaterial(matPIL1);
        geomPIL1.move(7.5f, 3, 18);
        
        //Creación del pilar 2
        Box pilar2 = new Box(0.4f,2.5f,0.3f);
        Geometry geomPIL2 = new Geometry("geomPIL1", pilar2);
        Material matPIL2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matPIL2.setTexture("ColorMap", assetManager.loadTexture("Textures/torre.png"));
        geomPIL2.setMaterial(matPIL2);
        geomPIL2.move(7.5f, 3, 24);
        
        //Creación del pilar 3
        Box pilar3 = new Box(0.4f,2.5f,0.3f);
        Geometry geomPIL3 = new Geometry("geomPIL", pilar3);
        Material matPIL3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matPIL3.setTexture("ColorMap", assetManager.loadTexture("Textures/torre.png"));
        geomPIL3.setMaterial(matPIL3);
        geomPIL3.move(-7.5f, 3, -6);
        
        //Creación del pilar 4
        Box pilar4 = new Box(0.4f,2.5f,0.3f);
        Geometry geomPIL4 = new Geometry("geomPIL1", pilar4);
        Material matPIL4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matPIL4.setTexture("ColorMap", assetManager.loadTexture("Textures/torre.png"));
        geomPIL4.setMaterial(matPIL4);
        geomPIL4.move(-7.5f, 3, 0);
        
        //Creación del pilar 5
        Box pilar5 = new Box(0.4f,2.5f,0.3f);
        Geometry geomPIL5 = new Geometry("geomPIL1", pilar5);
        Material matPIL5 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matPIL5.setTexture("ColorMap", assetManager.loadTexture("Textures/torre.png"));
        geomPIL5.setMaterial(matPIL5);
        geomPIL5.move(-7.5f, 3, 6);
        
        //Creación del ENEMIGO
        Sphere enemigo = new Sphere(60,60,0.5f);
        geomENE = new Geometry("geomENE", enemigo);
        Material matENE = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matENE.setTexture("ColorMap", assetManager.loadTexture("Textures/lava.jpg"));
        geomENE.setMaterial(matENE);
        geomENE.move(0, 3, 60);//Ponerlo en una posición
        
 
                
        //Adjuntamos los elementos del mapa al nodo principal
        rootNode.attachChild(geom); //terreno
        rootNode.attachChild(geomTOR); //torre
        //rootNode.attachChild(geomTOR2);
        rootNode.attachChild(geomPIL); //pilar0
        rootNode.attachChild(geomPIL1);//pilar1
        rootNode.attachChild(geomPIL2);//pilar2
        rootNode.attachChild(geomPIL3);//pilar3
        rootNode.attachChild(geomPIL4);//pilar4
        rootNode.attachChild(geomPIL5);//pilar5
        
        rootNode.attachChild(pruebaG);//torreParada 1
        rootNode.attachChild(pruebaG1);//torreParada 2
        
        rootNode.attachChild(geomENE); //ENEMIGO
        
       
        attachCenterMark();//DEL PUNTERO
        
    }
    
    //Agragamos el listener analógico ya que la acción de rotación será una acción continua
    private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float intensity, float tpf) {
            
            //Se comprueba que el trigger identificado corresponda a la acción deseada
            if(name.equals(MAPPING_ROTATE)){
                
                //En esta sección determinamos la acción de rotar la caja en la que esté a la mira del mouse.
                //Colision --> identificará el objeto al cual se le hace click
                CollisionResults results = new CollisionResults();
                
                //Se proyecta una línea de acuerdo a la posición de la cámara dirección donde la camara está apuntando
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                
                //Calculamos si está ray proyectando hace colision con el objeto.
                rootNode.collideWith(ray, results);
                
                //Si el usuario ha hecho click en algo, identificamos la geometía.
                if (results.size()>0){
                    Geometry target = results.getClosestCollision().getGeometry();

                    //Si el nombre de la geometría identificado es igual a "geomENE"
                    if(target.getName().equals("geomENE")){
                        
                        eliminado = true; //La variable "eliminado" cambia a verdadero para saber que el usuario dio clic 
                                          //en la geometria "geomENE"
                        
                        numeroMuertes ++; //Aumenta en 1 la variable "numeroMuertes" para utilizarla en contabilizar el numero
                                          //de muertes y así poder terminar el JUEGO.
                    }
                 
                }

            }
        }
        
    };

    @Override
    public void simpleUpdate(float tpf) {
        
        //Aqui se manipula todo lo relativo a la actividad del ENEMIGO.
        
        int posicionEZ ; // para guardar la posición de ejeZ
        
        geomENE.move(0, 0, -tpf * aceleradortpf);//Da MOVIMIENTO a la geometría del ENEMIGO
        Vector3f posicion = geomENE.getLocalTranslation(); //Obtenemos la posición de la geometría del ENEMIGO
        posicionEZ = (int) posicion.getZ(); //a "posicionEZ" le asignamos el valor del ejeZ
        
        /**
         * Si se clickeo sobre la geometría del enemigo --> geomENE se elimina al mismo.
         */           
        if(eliminado == true){
            rootNode.detachChild(geomENE); //Aqui se elimina al enemigo
   
            eliminado = false; //El estado de "eliminado" pasa a ser falso para reiniciar la variable y 
                               //cambiarla si nuevamente se elimina un ENEMIGO
            rootNode.attachChild(geomENE); //Vuelve a poner al ENEMIGO en el espacio.
            
            geomENE.setLocalTranslation(posicionX, posicionY, (posicionEZ * tpf) + 30 );//Damos posición al ENEMIGO

            aceleradortpf = aceleradortpf +1.8f; //Aumentamos 1.8f la velocidad de aparición del ENEMIGO
            posicionX = posicionX + 1; //Aumentamos en 1 la posición X del ENEMIGO
            posicionY = posicionY + 2; //Aumentamos en 1 la posición Y del ENEMIGO
            
            //Comparamos si el número de muertes es igual a 3 o 6 para cambiar la posición
            //de aparición del ENEMIGO.
            if(numeroMuertes == 3 || numeroMuertes == 6){
                posicionY = 2;
                posicionX = 0;
            }

        }
        
        /**
        * Condición que compara si la posición del ejeZ del ENEMIGO choca con la torre
        * Si es así termina el juego eliminando a TODOS los elementos.
        */
        if(posicionEZ  == -15 ){ 
            rootNode.detachAllChildren(); //Elimina todos los elementos del NODO principal.
 
            System.out.println("Perdiste"); 
            
            //Cambiamos la imagen de la pantalla cuando el JUGADOR pierde la partida.
            Picture cambioImagen = new Picture("cambioImagen");
            cambioImagen.setImage(assetManager, "Interface/torre1.png", true);
            cambioImagen.setWidth(settings.getWidth()/2);
            cambioImagen.setHeight(settings.getHeight()/2);
            cambioImagen.setPosition(settings.getWidth()/4, settings.getHeight()/4);
            guiNode.attachChild(cambioImagen);//Agregamos la imagen al nodo principal
        }
             
        /**
        * Si el usuario elimina a 8 ENEMIGOS gana el juego
        * Por lo tanto se compara si numeroMuertes = 8 y así termina.
        */
        if(numeroMuertes == 8){
            rootNode.detachAllChildren(); //Elimina todos los hijos del nodo principal
            
            //Cambiamos la imagen de la pantalla cuando el JUGADOR pierde la partida.
            Picture cambioImagen = new Picture("cambioImagen"); 
            cambioImagen.setImage(assetManager, "Interface/torre2.png", true);
            cambioImagen.setWidth(settings.getWidth()/2);
            cambioImagen.setHeight(settings.getHeight()/2);
            cambioImagen.setPosition(settings.getWidth()/4, settings.getHeight()/4);
            guiNode.attachChild(cambioImagen);//Agregamos la imagen al nodo principal
        }   

    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
