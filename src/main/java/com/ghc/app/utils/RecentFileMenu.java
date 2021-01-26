package com.ghc.app.utils;

import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
*
*/
public class RecentFileMenu extends JMenu {
/**
* Maximum width in pixels of text in menu items. File names are shortened to fit this width
*/
public int MAX_WIDTH_PIXELS = 450;
/**
* Maximum number of files allowed in recent file menu
*/
private int myMaxNumFiles;
/**
* List of file menu listeners
*/
private Vector<FileMenuListener> myListeners;
/**
* true if menu shall be disabled automatically when it contains no items.
* false if menu shall always stay enabled.
*/
private boolean myAutoDisable = true;
//---------------------------------------------------------------
public RecentFileMenu( String name ) {
this(name,0);
}
public RecentFileMenu( String name, int maxNumFiles ) {
super( name );
myMaxNumFiles = maxNumFiles;
myListeners = new Vector<FileMenuListener>();
setEnabled(false);
}
public void addFile( String filename ) {
if( super.getItemCount() == 0 ) {
super.setEnabled(true);
}
//    String fileName = file.getAbsolutePath();
boolean found = false;
int itemPos = -1;
int nItems = super.getItemCount();
for( int i = 0; i < nItems; i++ ) {
if( super.getItem(i) instanceof RecentFileItem ) {
if( ((RecentFileItem)getItem(i)).fileName.compareTo( filename ) == 0 ) {
found = true;
itemPos = i;
break;
}
} else {  }
}
if( !found ) {
if( super.getItemCount() == myMaxNumFiles ) {
super.remove( myMaxNumFiles-1 );
}
} else if( itemPos != 0 ) { super.remove( itemPos );
} else { return; }
RecentFileItem item = new RecentFileItem( filename );
super.add( item, 0 );
updateMnemonics( 0 );
}
public void removeFile( String filename ) {
int nItems = super.getItemCount();
for( int i = 0; i < nItems; i++ ) {
if( super.getItem( i ) instanceof RecentFileItem ) {
if( ( ( RecentFileItem )getItem( i ) ).fileName.compareTo( filename ) == 0 ) {
super.remove( i );
updateMnemonics(i);
break;
}
}
}
if( super.getItemCount() == 0 && myAutoDisable ) {
super.setEnabled(false);
}
}
public ArrayList<String> getFileList() {
int nItems = getItemCount();
ArrayList<String> list = new ArrayList<String>(nItems);
for( int i = 0; i < nItems; i++ ) {
if( getItem(i) instanceof RecentFileItem ) {
list.add( ((RecentFileItem)getItem( i )).fileName );
}
}
return list;
}
public int getMaxNumFiles() {
return myMaxNumFiles;
}
public void setMaxNumFiles( int maxNumFiles ) {
myMaxNumFiles = maxNumFiles;
}
public void setAutoDisable( boolean doSetAutoDisable ) {
if( myAutoDisable != doSetAutoDisable && getItemCount() == 0) {
if( doSetAutoDisable ) {
setEnabled(false);
}
else {
setEnabled(true);
}
}
myAutoDisable = doSetAutoDisable;
}
private void updateMnemonics( int startPos ) {
int nItems = super.getItemCount();
for( int i = startPos; i < nItems; i++ ) {
if( super.getItem( i ) instanceof RecentFileItem ) {
RecentFileItem item = ( RecentFileItem )getItem( i );
item.setText( (i+1) + " " + item.shortName );
item.setMnemonic( getPositionMnemonic(i) );
item.position = i;
}
}
}
private int getPositionMnemonic( int pos ) {
int mnemonic = -1;
if( pos < 9 ) {
switch( pos ) {
case 0:
mnemonic = KeyEvent.VK_1;
break;
case 1:
mnemonic = KeyEvent.VK_2;
break;
case 2:
mnemonic = KeyEvent.VK_3;
break;
case 3:
mnemonic = KeyEvent.VK_4;
break;
case 4:
mnemonic = KeyEvent.VK_5;
break;
case 5:
mnemonic = KeyEvent.VK_6;
break;
case 6:
mnemonic = KeyEvent.VK_7;
break;
case 7:
mnemonic = KeyEvent.VK_8;
break;
case 8:
mnemonic = KeyEvent.VK_9;
break;
}
}
return mnemonic;
}
private void moveToFirstPosition( RecentFileItem item ) {
int nItems = super.getItemCount();
for( int i = 0; i < nItems; i++ ) {
if( getItem( i ).equals( item ) ) {
if( item.position != 0 ) {
super.remove( i );
super.add( item, 0 );
updateMnemonics( 0 );
}
break;
}
}
}
public void addFileMenuListener( FileMenuListener listener ) {
if( !myListeners.contains(listener) ) {
myListeners.add(listener);
}
}
public void removeFileMenuListener( FileMenuListener listener ) {
if( myListeners.contains(listener) ) {
myListeners.remove(listener);
}
}
public void fireFileSelectedEvent( FileMenuEvent event ) {
for( int i = 0; i < myListeners.size(); i++ ) {
((FileMenuListener)myListeners.get(i)).fileSelected( event );
}
}
//------------------------------------------------------
class RecentFileItem extends JMenuItem {
String fileName;
String shortName;
int position = 0;
RecentFileItem( String theFilename ) {
super("TEMP_NAME");
this.fileName = theFilename;

FontMetrics metrics = getFontMetrics(getFont());
int size = metrics.stringWidth(fileName);
int charsToChop = (int)( ((double)(size-MAX_WIDTH_PIXELS))/(double)size * fileName.length() );
if( charsToChop > 0 ) {
shortName = "..." + fileName.substring( charsToChop );
}
else {
shortName = fileName;
}
setToolTipText( fileName );
addActionListener(new ActionListener() {
public void actionPerformed( ActionEvent e ) {
if( position != 0 ) moveToFirstPosition( (RecentFileItem)e.getSource() );
fireFileSelectedEvent( new FileMenuEvent(e.getSource(),new File(fileName)) );
}
});
}
}
}

