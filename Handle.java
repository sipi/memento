import java.awt.*;

/*  
 *  Copyright © 2008-2011 Sipieter Clément <c.sipieter@gmail.com>
 *  Copyright © 2011 Sellem Lev-Arcady
 *
 *  This file is part of Memento.
 *
 *  Memento is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Memento is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Memento.  If not, see <http://www.gnu.org/licenses/>.
 */
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Handle extends JPanel{
    
    /**
     * the instance of memento
     */
    Memento memento;
            
    public Handle(Memento memento){
        this.memento = memento;
        this.addMouseListener(new HandleObserver(this));
    }
    
    public void setHandleLocation(int x, int y){
        this.memento.setLocation(x, y);
    }
    
    public Point getHandleLocation(){
        return this.memento.getLocation();
    }
    
    
    //Class d'écoute des actions de la souris
    private class HandleObserver extends MouseAdapter {
        
        public static final int FPS = 50;
        
        Handle handle;
        Timer timer;
        
        public HandleObserver(Handle h) {
            this.handle = h;
        }
        
        @Override
        public void mousePressed(MouseEvent e){
            Point mouse_position = new Point(MouseInfo.getPointerInfo().getLocation());
            timer = new Timer();
            timer.schedule(new HandleController(this.handle.memento, mouse_position), 0, 1000/FPS);
        }
            
        public void mouseReleased(MouseEvent e){
           timer.cancel();
        }
        
    }
    
    
    
    //class de temporisation de l'animation de déplacement de la fenêtre
    private class HandleController extends TimerTask{
        
        Memento memento;
        Point old_mouse_position, window_position;
        
        HandleController(Memento m, Point old_mouse_position){
            this.memento = m;
            this.window_position = new Point(this.memento.getLocation());
            this.old_mouse_position =  old_mouse_position;
        }

        @Override
        public void run() {
            this.memento.setLocation(
                    (int)(this.window_position.getX() + MouseInfo.getPointerInfo().getLocation().getX() - this.old_mouse_position.getX()),
                    (int)(this.window_position.getY() + MouseInfo.getPointerInfo().getLocation().getY() - this.old_mouse_position.getY())
            );
           
        }
    }
}