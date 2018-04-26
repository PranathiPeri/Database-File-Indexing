
/*
 * Team Members : Pranathi Peri, Sai Krishna
 * This program is used to  create an index file ,
  * find a key in index file ,
  * insert a new record in the original file  and index
  * List the records   
  * Authors: Pranathi Peri ,Sai Krishna Reddy
*/
package dbassignment;
import java.io.*;
import java.util.*;

/* This is used to create an index structure 
 * Authors: Pranathi Peri ,Sai Krishna Reddy
*/
class IndexStructure{
    private int keyLength;
    private String inputFilePath;
    private boolean isMetaDataFile;
    private int valuesSet;

    IndexStructure(){
        valuesSet =0;
    }

    public int getkeyLength(){
        return keyLength;
    }

    public String getInputFilePath(){
        return inputFilePath;
    }

    public boolean getisMetaDataFile(){
        return isMetaDataFile&&(valuesSet==3);
    }

    public void setkeyLength(int in){
        keyLength = in;
    }

    public void setInputFilePath(String in){
        inputFilePath = in;
    }

    public void setisMetaDataFile(boolean in){
        isMetaDataFile = in;
    }

    public void setvaluesSet(int in){
        valuesSet = in;
    }
}
/*
 This method is used to create an index
* Authors: Pranathi Peri ,Sai Krishna Reddy
*/

class Index{

    private BPTree<String,Long> btree;
    RandomAccessFile inputFile = null;
    RandomAccessFile indexFile = null;

    public Index(){
        btree = new BPTree<String,Long>();
    }

    public void create(String fileName,String indexfile,int keyLength){
        try {
            inputFile = new RandomAccessFile(fileName,"r");
            indexFile = new RandomAccessFile(indexfile,"rw");
            String str;
            Long position = 0L;
            Long prevPosition = 0L;
            while ((str = inputFile.readLine()) != null) {
                if(!str.equalsIgnoreCase("")){
                    str=str.substring(0,keyLength);
                    btree.put(str,position);
                    position = inputFile.getFilePointer();
                }
            }
            indexFile.writeBytes("isMetadataFile=true");
            indexFile.writeBytes(",fileName="+fileName);
            indexFile.writeBytes(",keyLength="+Integer.toString(keyLength));
            SortedMap<String,Long> values = btree.tailMap(btree.firstKey());
            for (Map.Entry<String,Long> value:values.entrySet()) {
                indexFile.writeBytes("\n");
                indexFile.writeBytes("Key="+value.getKey()+","+"Offset="+Long.toString(value.getValue()));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        finally {

            try {
                inputFile.close();
                indexFile.close();
                File f=new File(indexfile);
                System.out.println("Index file created at"+f.getAbsolutePath());
            }

            catch (Exception x) {
                x.printStackTrace();
            }

        }
    }
/* 
 *This method is used to find a key in the index file
 * Authors: Pranathi Peri ,Sai Krishna Reddy
*/
    public void find(String indexfile,String key){
        try{
            indexFile = new RandomAccessFile(indexfile,"r");
            String metaData = indexFile.readLine();
            IndexStructure idx = validateIndexFile(metaData);

            if(idx.getisMetaDataFile()){
                inputFile = new RandomAccessFile(idx.getInputFilePath(),"r");
                loadIndexFile(indexFile,idx.getkeyLength());

                Long position = btree.get(key);
                if(position!=null){
                    inputFile.seek(position);
                    System.out.println(inputFile.readLine());
                }
                else{
                    System.out.println("Given Key is not Found");
                }
            }
            else{
                throw new Exception("Please enter a valid index file for searching");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            close();
        }
    }

    private IndexStructure validateIndexFile(String metaData) throws Exception{
        IndexStructure idx = new IndexStructure();

        String[] arrmetaData = metaData.split(",");
        int valuesSet = 0;
        if(arrmetaData.length== 3){
            for(int i=0;i<3;i++){
                String[] each = arrmetaData[i].split("=");
                if(each.length==2){
                    if(each[0].equalsIgnoreCase("fileName")){
                        idx.setInputFilePath(each[1]);
         //        inputFile = new RandomAccessFile(each[1],"r");
                        valuesSet++;
                    }
                    else if(each[0].equalsIgnoreCase("isMetadataFile")){
                        if(each[1].equalsIgnoreCase("true")){
                            idx.setisMetaDataFile(true);
                            valuesSet++;
                        }
                        else{
                            idx.setisMetaDataFile(false);
                            throw new Exception("Please enter a valid index file for searching");
                        }
                    }
                    else if(each[0].equalsIgnoreCase("keyLength")){
                        idx.setkeyLength(Integer.parseInt(each[1]));
                        valuesSet++;
                    }
                    idx.setvaluesSet(valuesSet);
                }
                else{
                    throw new Exception("Please enter a valid index file for searching");
                }
            }
        }
        else{
            throw new Exception("Please enter a valid index file for searching");
        }
        return idx;
    }
/*
 *This method is used load Index file 
  * Authors: Pranathi Peri ,Sai Krishna Reddy 
*/
    private void loadIndexFile(RandomAccessFile indexFile,int keyLength){
        String str;
        try {
            while ((str = indexFile.readLine()) != null) {
                String[] arr = str.split(",");
                if(arr.length == 2){
                    String key="";
                    Long value=-1L;
                    for(int i=0;i<2;i++){
                        String[] each = arr[i].split("=");
                        if(each[0].equalsIgnoreCase("Key")&&each[1].length() == keyLength){
                            key=each[1];
                        }
                        else if(each[0].equalsIgnoreCase("Offset")) {
                            value = Long.parseLong(each[1]);
                        }
                    }
                    if(value>=0L && !key.equalsIgnoreCase("")){
                        btree.put(key,value);
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
/*
* This method is used to insert new record into the file
* Authors: Pranathi Peri ,Sai Krishna Reddy
*/

    public void insertNew(String indexfile,String newRecord){
        IndexStructure idx = null;
        try{
            indexFile = new RandomAccessFile(indexfile,"r");
            String metaData = indexFile.readLine();
            idx = validateIndexFile(metaData);

            if(idx.getisMetaDataFile()){
                File f = new File(idx.getInputFilePath());
                long fileLength = f.length();
                RandomAccessFile raf = new RandomAccessFile(f, "rw");
                raf.seek(fileLength);
                raf.writeBytes("\n"+newRecord);
                raf.close();
            }
            else{
                throw new Exception("Please enter a valid index file");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            close();
        }

        create(idx.getInputFilePath(),indexfile,idx.getkeyLength());
        System.out.println(newRecord+" added");

    }
/*
* This method is used to list records in the file.
* Authors: Pranathi Peri ,Sai Krishna Reddy
*/
    public void ListItems(String indexfile,String searchKey,int recordCount){
        try{
            indexFile = new RandomAccessFile(indexfile,"r");
            String metaData = indexFile.readLine();
            IndexStructure idx = validateIndexFile(metaData);

            if(idx.getisMetaDataFile()){
                inputFile = new RandomAccessFile(idx.getInputFilePath(),"r");
                loadIndexFile(indexFile,idx.getkeyLength());

                SortedMap<String,Long> keys =  btree.tailMap(searchKey);
                Set<Map.Entry<String,Long>> entrySet = keys.entrySet();
                int i=0;
                for (Map.Entry<String,Long> key:entrySet) {
                    if(i == recordCount){
                        break;
                    }
                    Long position = key.getValue();
                    if(position!=null){
                        inputFile.seek(position);
                        System.out.println(inputFile.readLine());
                        i++;
                    }
                }
            }
            else{
                throw new Exception("Please enter a valid index file for searching");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally {
            close();
        }
    }

    private void close(){
        try {
            if(inputFile!=null)
                inputFile.close();
            if(indexFile!=null)
                indexFile.close();
        }
        catch (Exception x) {
            x.printStackTrace();
        }
    }

}
/*
* In the main Method we are validating the command line arguments
* and making the corresponding calls to the required methods.
* Authors: Pranathi Peri ,Sai Krishna Reddy
*/
public class Main extends Index {

    public static void main(String[] args) {
	// write your code here
        File inpfile=new File(args[1]);
        Index indexObj = new Index();

        if(args[0].equalsIgnoreCase("-create"))
        {
            //arg[0] - -create
            //arg[1] - input filename
            //arg[2] - indexfile
            //arg[3] - keylength

            if(args.length!=4) {
                System.out.println("No valid arguments");
                return;
            }
            if(!inpfile.exists()) {
                System.out.println("File does not exist");
                return;
            }
            if(!args[3].matches("[0-9]*")) {
                System.out.println("Key Length must be entered in numbers");
                return;
            }
            indexObj.create(args[1],args[2],Integer.parseInt(args[3]));
        }
        else if(args[0].equalsIgnoreCase("-find"))
        {
            //-find cs6360.idx 45526813100142A
            //arg[0] -find
            //arg[1] indexfile
            //arg[2] Key
            if(args.length!=3){
                System.out.println("No valid arguments");
                return;
            }

            if(!inpfile.exists()){
                System.out.println("Index file does not exist");
                return;
            }

            indexObj.find(args[1],args[2]);
        }
        else if(args[0].equalsIgnoreCase("-insert"))
        {
            //-insert CS6360Asg5.indx "12222222222222C test data I added"
            //arg[0] -insert
            //arg[1] indexFile
            //arg[2] new String

            if(args.length!=3){
                System.out.println("No valid arguments");
                return;
            }

            if(!inpfile.exists()) {
                System.out.println("Index file does not exist");
                return;
            }

            indexObj.insertNew(args[1],args[2]);
        }
        else if(args[0].equalsIgnoreCase("-list"))
        {
            //-list CS6360Asg5.indx 38417813544394A 12
            //arg[0] -list
            //arg[1] indexFile
            //arg[2] key
            //arg[3] count
            if(args.length!=4){
                System.out.println("No valid arguments");
                return;
            }

            if(!inpfile.exists()) {
                System.out.println("Index file does not exist");
                return;
            }

            if(!args[3].matches("[0-9]*")) {
                System.out.println("Please enter valid number");
                return;
            }

            indexObj.ListItems(args[1],args[2],Integer.parseInt(args[3]));

        }





    }
}