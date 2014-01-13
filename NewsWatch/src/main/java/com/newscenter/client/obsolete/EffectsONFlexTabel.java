package com.newscenter.client.obsolete; 
 
import com.google.gwt.user.client.DOM; 
import com.google.gwt.user.client.Element; 
import com.google.gwt.user.client.Event; 
import com.google.gwt.user.client.ui.FlexTable; 
import com.google.gwt.user.client.ui.Widget; 
 

public class EffectsONFlexTabel extends FlexTable 
{ 
 private FlexCellFormatter formatter = getFlexCellFormatter(); 
 private RowFormatter rowFormatter = getRowFormatter(); 
 private String hoverStyle = null; 
 private boolean hasHeader=false; 
 private boolean clickRow ; 
 //public static final String STYLE_TABLE="GridTable"; 
 //public static final String STYLE_HEADER="GridHeader"; 
 public static final String STYLE_ROW_HOVER="effectOnFlexMouseEnter"; 
 public EffectsONFlexTabel() 
 { 
  //setStylePrimaryName(STYLE_TABLE); 
  setHoverStyleName(STYLE_ROW_HOVER); 
  sinkEvents(Event.ONMOUSEOVER); 
  sinkEvents(Event.ONMOUSEOUT); 
  sinkEvents(Event.ONCLICK); 
 } 
  public String getHoverStyleName() { 
        return hoverStyle; 
    } 
 
    public void setHoverStyleName(String style) { 
        hoverStyle = style; 
    } 
  
 public void onBrowserEvent(Event event) { 
     switch (DOM.eventGetType(event)) { 
         case Event.ONMOUSEOVER: { 
             // Find out which cell was entered. 
             Element td = getEventTargetCell(event); 
             
             if (td == null) 
                 return; 
             Element tr = DOM.getParent(td); 
             if (tr == null) 
                 return; 
             Element body = getBodyElement(); 
             int row = DOM.getChildIndex(body, tr); 
             int col = DOM.getChildIndex(tr,td); 
             // Fire the event 
             mouseEntersRow(this, row,col); 
             //onMouseEnter(this); 
             break; 
         } 
          
         case Event.ONMOUSEOUT: { 
             // Find out which cell was exited. 
             Element td = getEventTargetCell(event); 
             if (td == null) 
                 return; 
             Element tr = DOM.getParent(td); 
             if (tr == null) 
                 return; 
             Element body = getBodyElement(); 
             int row = DOM.getChildIndex(body, tr); 
             int col = DOM.getChildIndex(tr,td); 
             // Fire the event. 
             mouseLeavesRow(this, row,col); 
             break; 
         } 
          
          
          
          
          
         case Event.ONCLICK: { 
             // Find out which cell was exited. 
             Element td = getEventTargetCell(event); 
             if (td == null) 
                 return; 
             Element tr = DOM.getParent(td); 
             if (tr == null) 
                 return; 
             Element body = getBodyElement(); 
            // int col = DOM.getElementPropertyInt(td, "td"); 
             int row = DOM.getChildIndex(body, tr); 
             int col = DOM.getChildIndex(tr,td); 
             String labelText = DOM.getInnerText(td); 
             // Fire the event. 
            // mouseClick(labelText, row, col); 
             mouseOnClickEnter(this, row,col); 
             break; 
         } 
      } 
      super.onBrowserEvent(event); 
  } 
 
 
  
 public void onMouseDown(Widget arg0, int arg1, int arg2) { 
   
  //rowFormatter.setStylePrimaryName(arg1, "effectOnFlexMouseEnter"); 
 } 
 
 
  
 public void onMouseEnter(Widget arg0) { 
   
  //setStylePrimaryName("effectOnFlexMouseEnter"); 
 } 
 
 
  
 public void onMouseLeave(Widget arg0) { 
   
  //setStylePrimaryName("effectOnFlexMouseLeave"); 
 } 
 
 
  
 public void onMouseMove(Widget arg0, int arg1, int arg2) { 
  //setStylePrimaryName("effectOnFlexMouseEnter"); 
  //rowFormatter.setStylePrimaryName(arg1, "effectOnFlexMouseEnter"); 
 } 
 
 
  
 public void onMouseUp(Widget arg0, int arg1, int arg2) { 
   
  //rowFormatter.setStylePrimaryName(arg1, "effectOnFlexMouseLeave"); 
 } 
  private void mouseEntersRow(Widget sender, int row,int col) { 
   if(hasHeader && row==0) 
            return; 
        if (hoverStyle != null) 
           // this.getRowFormatter().addStyleName(row, hoverStyle); 
         this.getCellFormatter().addStyleName(row, col, hoverStyle); 
    } 
   
  private void mouseLeavesRow(Widget sender, int row,int col) { 
        if(hasHeader && row==0) 
            return; 
        if (hoverStyle != null) 
           // this.getRowFormatter().removeStyleName(row, hoverStyle); 
         this.getCellFormatter().removeStyleName(row, col, hoverStyle); 
    } 
   
  private void mouseOnClickEnter(Widget sender, int row,int col) 
  { 
   if(clickRow==true) 
   { 
    System.out.println("ON click is true"); 
    //this.getRowFormatter().removeStyleName(row, "effectOnFlexMouseLeave"); 
   // this.getCellFormatter().removeStyleName(row,col, "effectOnFlexMouseLeave"); 
    clickRow = false; 
   } 
   else 
   { 
    System.out.println("ON click is false"); 
    //this.getRowFormatter().addStyleName(row, "effectOnFlexMouseLeave"); 
   // this.getCellFormatter().addStyleName(row,col, "effectOnFlexMouseLeave"); 
    clickRow = true; 
   } 
  } 
 
} 
