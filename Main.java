
import java.util.*;
import java.io.*;


/*
 * CORPUS is reuters21578
 * It has 21 .sgm files with each file having 1000 documents
 * We have used Stemmer.java and Spelling.java for stemming and spelling correction respectively
 * To tokenize we used StringTokenizer
 * For hashing we used the HashMap data structure in java
 * 
 * 
 * 
 * 
 * 
 * Group members 
 * 
 * 1. Anshul Chhabra (2013B2A7803P)
 * 2. Harshit Oberoi (2013B1A7865P)
 * 3.
 * 4.
 * 5.
 */


/*
 * Document class
 * title is the title of the various documents in the corpus , Some documents do not have title so this can be null
 * id is the Newid of the various documents in the corpus
 * pos is an ArrayList to store the positions where the specific term is occuring in the document 
 * tfidf is a helper function in the doc class to calculate the (cosine tfidf) of the document
 * compareTo function is implemented to compare 2 documents based in the cosine tfidf value and then sort the
 * to display the best 20 results to the user.
 */
class doc implements Comparable<doc>{
	String title;
	int id;	
	ArrayList<Integer> pos;
	doc(String title,int id){
		this.title=title;
		this.id=id;
		pos=new ArrayList<Integer>();		
	}
	
	/*
	 * helper function to calculate the cosine tfidf of document
	 * value1 has the final tfidf value
	 * arr is the array of terms in the query
	 * tfq is to store the tf value for the query
	 * tf is to store the tf value for the document
	 * */
	public double tfidf(){
		double value1=0;
		String arr[]=Main.corrected_query.split(" ");
		for (int i = 0; i < arr.length; i++) {
			double tfq = 1;
			for(int j = 0 ; j < arr.length ; j++){
				if(arr[j].equals(arr[i])){
					tfq++;
				}
			}
			
			tfq = tfq / arr.length;
			
			if(Main.token_hash.get(arr[i])==null){
				continue;
			}
			
			doc temp=Main.binary_search(Main.token_hash.get(arr[i]),this.id);
			double tf;
			if(temp!=null){
				tf=temp.pos.size();
			}				
			else{
				tf=1;
			}
			
			double total_terms;
			if(temp!=null){
				total_terms=Main.index_to_doc.get(temp.id).split(" ").length;
			}	
			else{
				total_terms = 1;
			}				
			double totaldocs=21600;
			double idf=Main.token_hash.get(arr[i]).size();
			tf = tf/total_terms;
			idf = Math.log(totaldocs/idf);			
			value1 = value1 + ((tf*idf) * (tfq*idf));
			value1 = value1 / total_terms;
		}
		return value1;
	}
	
	/*
	 * doc class implements the comparable interface so compareTo function has to be implemented
	 * this function take a document as an argument and compares the documents based on the cosine tfidf values
	 * returns 0 if both have equal rank
	 * returns 1 and -1 correspondingly if other document has rank less and more then the document
	 */
	public int compareTo(doc d){
		double value1=0,value2=0;
		String arr[]=Main.corrected_query.split(" ");
		for (int i = 0; i < arr.length; i++) {
			
			double tfq = 1;
			for(int j = 0 ; j < arr.length ; j++){
				if(arr[j].equals(arr[i])){
					tfq++;
				}
			}
			tfq = tfq / arr.length;			
			if(Main.token_hash.get(arr[i])==null){
				continue;
			}
			doc temp=Main.binary_search(Main.token_hash.get(arr[i]),this.id);
			double tf;
			if(temp!=null)
				tf=temp.pos.size();
			else
				tf=1;
			double total_terms;
			if(temp!=null)
				total_terms=Main.index_to_doc.get(temp.id).split(" ").length;
			else
				total_terms = 1;
			double totaldocs=21600;
			double idf=Main.token_hash.get(arr[i]).size();
			tf = tf/total_terms;
			idf = Math.log(totaldocs/idf);
			
			value1 = value1 + ((tf*idf) * (tfq*idf));
			value1 = value1 / total_terms;
		}
		
		for (int i = 0; i < arr.length; i++) {
			
			double tfq = 1;
			for(int j = 0 ; j < arr.length ; j++){
				if(arr[j].equals(arr[i])){
					tfq++;
				}
			}
			tfq = tfq / arr.length;
			
			
			if(Main.token_hash.get(arr[i])==null){
				continue;
			}
			doc temp=Main.binary_search(Main.token_hash.get(arr[i]),d.id);
			double tf;
			if(temp!=null)
				tf=temp.pos.size();
			else
				tf=1;
			double total_terms;
			if(temp!=null)
				total_terms=Main.index_to_doc.get(temp.id).split(" ").length;
			else
				total_terms = 1;
			double totaldocs=21600;
			double idf=Main.token_hash.get(arr[i]).size();
			
			tf = tf/total_terms;
			idf = Math.log(totaldocs/idf);
			value2= value2 + ((tf*idf) * (tfq*idf));
			value2 = value2 / total_terms;
		}
		
		
		
		if(value1>value2){
			return 1;
		}
		else if(value1<value2){
			return -1;
		}
		else{
			return 0;
		}
	}
}

/*
 * END OF THE DOC CLASS
 * 
 */


/*
 * MAIN CLASS STARTS
 * corrected_query is the final query after stemming and spelling correction
 * token_hash is the Hashmap that stores all the terms that appear in the corpus
 * each term(key) has an (arraylist of  documents)(Value) in which this term appears
 * doc_to_index is the HashMap to index the documents
 * index_to_doc is the Hashmap to get the document back from the index
 */

public class Main{
	static String corrected_query;
	static HashMap<String,ArrayList<doc>> token_hash;
	static HashMap<String,Integer> doc_to_index;
	static HashMap<Integer,String> index_to_doc; 
	static int totaldocs=0;
	
	/*
	 * to search document in the term arraylist we used binary search 
	 * and also to search the position in the positions arraylist of the term
	 * so complexity of search is O(logn)
	 */
	public static int binary_search_pos(ArrayList<Integer> al,int num){
		int low=0;
		int high=al.size()-1;
		int mid;
		while(low<=high){
			mid=(low+high)/2;
			if(al.get(mid) == num){
				return mid;
			}
			else if(al.get(mid) < num){
				low=mid+1;
			}
			else{
				high=mid-1;
			}
		}
		return -1;
	}
	
	public static doc binary_search(ArrayList<doc> al,int id){
		int low=0;
		int high=al.size()-1;
		int mid;
	
		while(low<=high){
			mid=(low+high)/2;
			if(al.get(mid).id == id){
				return al.get(mid);
			}
			else if(al.get(mid).id < id){
				low=mid+1;
			}
			else{
				high=mid-1;
			}
		}
		return null;	
	}
	
	
	/* To do spelling correction big.txt is a file in which all the documents are combined into 1
	 * so the spell corrector uses this as a database to apply Edit Distance algorithm and correct the spelling
	 * doc_id is the hashed id of the document
	 * tem is a string that stores the whole body of the document
	 * */
	public static void main(String[] args) throws IOException {
		double finalFmeasure=0;
		double finalmrr=0;
		double finalmap=0;
		Stemmer s=new Stemmer();
		token_hash=new HashMap<String,ArrayList<doc>>();
		doc_to_index = new HashMap<String,Integer>();
		index_to_doc=new HashMap<Integer,String>();		
		BufferedWriter bw = new BufferedWriter(new FileWriter("big.txt"));
		int doc_id=0;
		for(int i=0;i<=21;i++){
			BufferedReader br;
			if(i<10){
				br=new BufferedReader(new FileReader("reut2-0"+"0"+i+".sgm"));
			}
			else{
				br=new BufferedReader(new FileReader("reut2-0"+i+".sgm"));
			}
			String tem="";			
			String to_be_tokenized="";
			
			
			/*
			 * READING THE DOCS AND STORING TERMS IN HASH*/
			while(true){
				String s1=br.readLine();
				if(s1==null)
					break;				
				tem+=s1;
				bw.write(s1);
				StringTokenizer p=new StringTokenizer(s1, "%$\\., \"#!&^/*;+()=@[]{}_?:|~-<>");//Used for Tokenizing
				
				while(p.hasMoreTokens()){
					String token=s.add(p.nextToken());
					if(token.equals("oldid") ){
						int oldidindex=tem.lastIndexOf("OLDID");
						tem=tem.substring(0, oldidindex);
						totaldocs++;
						int start_of_title=tem.indexOf("<TITLE>");
						int end_of_title=tem.indexOf("</TITLE>");
						String title="";
						if(start_of_title==-1){
							title="";
						}
						else{
							title=tem.substring(start_of_title+7, end_of_title);;
						}
						int start_of_body=tem.indexOf("<TEXT>");
						int end_of_body=tem.indexOf("</TEXT>");
						String body="";
						if(start_of_body==-1){
							body="";
						}
						else{
							body=tem.substring(start_of_body+6, end_of_body);
						}
						
						doc_to_index.put(body, doc_id);	//Hashing doc to id
						index_to_doc.put(doc_id, body);	//Hashing id to doc
						tem=s1;
						
						StringTokenizer tokens=new StringTokenizer(to_be_tokenized);
						
						int position=0;		//Position of an word in a doc
						while(tokens.hasMoreTokens()){
							String t=tokens.nextToken();	//Indivisual token parsing
							//System.out.println(t);
							if(!token_hash.containsKey(t)){	//Hashing the tokens
								doc d=new doc(title,doc_id);	//Creating object for doc
								d.pos.add(position);
								token_hash.put(t, new ArrayList<doc>());	//Adding token into HashMap
								token_hash.get(t).add(d);
							}
							else{
								doc temp=binary_search(token_hash.get(t),doc_id);	//Token Already in HashMap. Checking if doc object of this document present
								if(temp==null){		//if temp null then this doc not present in arraylist
									doc d=new doc(title,doc_id);	//Creating object for doc
									d.pos.add(position);
									token_hash.get(t).add(d);	//adding this doc in arraylist
								}
								else{
									temp.pos.add(position);
								}
								
							}
							position++;
						}//end while-inner
						to_be_tokenized="";
						doc_id++;
					}//end if
					to_be_tokenized+=token+" ";
				}//end while-middle
			}//end while-top
		}//end for
		
		/*PROCESSING THE CORPUS ENDS*/
	    
		/*INPUTING THE QUERY*/		
		Spelling corrector = new Spelling("big.txt");
		BufferedReader input=new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter 1 for Normal Query and 2 for Proximity Query");
		int querynum=Integer.parseInt(input.readLine());
		//querynum is 1 for normal query search
		if(querynum==1){
			System.out.println("Enter query: ");
			String query=input.readLine();
			double mrr = 0; 
			double map = 0;
			int count = 0;
			
			/**/
			while(true){
				
			//Menu to display to user for normal query
			System.out.println("Enter 1 - to display tokens and normalized query");
			System.out.println("Enter 2 - to display result of spell correction");
			System.out.println("Enter 3 - to fetch documents");
			System.out.println("Enter 4 - to exit");
			int number=Integer.parseInt(input.readLine());
					
			/*Spell correction of the query
			 * processing the query inputed from the user
			 * corrected query is finally used
			 * result arraylist has the final result of documents*/
			String originalquery = query;
			
			originalquery=originalquery.trim();
			String correctedquery = "";
			StringTokenizer st = new StringTokenizer(originalquery," -,_$!&%");
			
			String nexttoken=st.nextToken();
			if(corrector.correct(nexttoken).equals(("?"+nexttoken))){
				
				correctedquery=nexttoken;
			}
			else{
				correctedquery = corrector.correct(nexttoken);
			}
			while(st.hasMoreTokens()){
				nexttoken=st.nextToken();
				if(corrector.correct(nexttoken).equals(("?"+nexttoken))){
					correctedquery = correctedquery + " " + nexttoken;
				}
				else{
					correctedquery = correctedquery + " " + (corrector.correct(nexttoken));			
				}
				
			}
			
			String toprint = correctedquery;
			String arr[] = correctedquery.split(" ");
			correctedquery="";
			for (int i = 0; i < arr.length; i++) {
				arr[i]=s.add(arr[i]);
				correctedquery+=arr[i]+" ";
			}
			corrected_query=new String(correctedquery);
			String relevancechecker=new String(correctedquery);
			ArrayList<doc> result=new ArrayList<doc>();		//Resultant ArrayList
			
			/* ANDing the documents means getting the documents which have all the tokens in the query
			 * Documents are ANDed to improve the search results 
			 * finaldocs has the final result of ANDing of documents
			 * finalsdoc is the helper arraylist used for doing the AND of the documents
			 * Anding is done by getting the result of each token and then putting the final result in finaldocs
			 */
			
			ArrayList<doc> finaldocs = new ArrayList<doc>();
			ArrayList<doc> finalsdoc = new ArrayList<doc>();
			String tokenwaaali=new String(correctedquery);
			if(correctedquery.split(" ").length==1){
				correctedquery=correctedquery+correctedquery;
				corrected_query=new String(correctedquery);			
			}
			st = new StringTokenizer(correctedquery);
			String word = st.nextToken();		
			finaldocs = token_hash.get(word);
			while(st.hasMoreTokens()){
				word = st.nextToken();
				if(token_hash.get(word)==null){
					continue;
				}
				ArrayList<doc> temp1 = token_hash.get(word);
				if(temp1!=null)
				for(int i=0;i<temp1.size();i++){
					
					doc dtemp = temp1.get(i);
					int did = dtemp.id;
					doc rtemp = binary_search(finaldocs, did);
					if(rtemp!=null){
						finalsdoc.add(rtemp);
					}
				}
				finaldocs = finalsdoc;
				finalsdoc=new ArrayList<doc>();			
			}
			if(!(finaldocs.size()==0)){
				Collections.sort(finaldocs,Collections.reverseOrder());		//Uses compareTo method in doc to sort
			}
			
			/*
			 * ANDING FINISHES
			 * ALSO THE FINALDOCS ARE SORTED BY THIS TIME BASED ON THE COSINE TFIDF SCORE
			 */
			
			
			/*CODE FOR HANDLING PHRASE QUERY STARTS
			 * arraylist positional has the final result of the documents that have the exact same phrase as 
			 * specified by the user in the query
			 * Result has sorted data of both phrase query and anding on the basis of cosine tfidf score*/
			ArrayList<doc> temp=new ArrayList<doc>();
			
			ArrayList<doc> positional = new ArrayList<doc>();
			for (int i = 0; i < finaldocs.size(); i++) {
				temp.add(finaldocs.get(i));
				String res="";
				String stemp=index_to_doc.get(finaldocs.get(i).id).toLowerCase();
				StringTokenizer stoken=new StringTokenizer(stemp, "%$\\., \"#!&^/*;+()=@[]{}_?:|~-<>");
				while(stoken.hasMoreTokens()){
					res+=s.add(stoken.nextToken())+" ";
				}
				res=res.trim();
				relevancechecker=relevancechecker.trim();
				if(res.indexOf(relevancechecker) != -1){
					positional.add(finaldocs.get(i));
					finaldocs.remove(i);
				}
			}
			for (int i = 0; i < positional.size(); i++) {
				result.add(positional.get(i));
			}
			
			for (int i = 0; i < finaldocs.size(); i++) {
				result.add(finaldocs.get(i));
			}
			
			ArrayList<doc> finaldocs2=new ArrayList<doc>();
			
			
			/*
			 * Checking if the result arraylist has more than or equal to 10 documents.
			 * If Not then ORing of the documents is done and sorted using cosine tfidf
			 */
			if((finaldocs.size()+positional.size())<10){
				st = new StringTokenizer(correctedquery);
				while(st.hasMoreTokens()){
					word = st.nextToken();
					if(token_hash.get(word)==null){
						continue;
					}
					ArrayList<doc> temp1=new ArrayList<doc>();
					temp1 = token_hash.get(word);
					if(temp1!=null)
					for(int i=0;i<temp1.size();i++){
						
						doc dtemp = temp1.get(i);
						int did = dtemp.id;
						doc rtemp=null;
						for (int j = 0; j < temp.size(); j++) {
							if(temp.get(j).id == did){
								rtemp=temp.get(i);
								break;
							}
						}
						if(rtemp==null){
							temp.add(dtemp);
							finaldocs2.add(dtemp);
						}
					}
								
				}
				
			}
			
			if(!(finaldocs2.size()==0)){
				Collections.sort(finaldocs2,Collections.reverseOrder());
			}
			
			
			for (int i = 0; i < finaldocs2.size() ; i++) {
				result.add(finaldocs2.get(i));
			}
			
			/*DIPLAYING THE USER RESULT BASED ON THE OPTION AND LOOPING UNTIL THE USER EXITS*/
			
				if(number==1){
					StringTokenizer w = new StringTokenizer(tokenwaaali, "%$\\., \"#!&^/*;+()=@[]{}_?:|~-<>");
					System.out.print("Tokens : ");
					while(w.hasMoreTokens()){
						
						System.out.print(s.add(w.nextToken()) + " ,");
					}
					System.out.println();
					System.out.println(correctedquery);
				}
				else if(number == 2){
					System.out.println(toprint);
				}
				else if(number == 3){
					count++;
					if(result.size()!=0){
						int size=result.size();
						if(size>20){
							size=20;
						}
						double relevant = positional.size();
						double retrieved = result.size();
						if(result.size() > 20){
							retrieved = 20;
						}
						if(positional.size()>=20){
							relevant=20;
						}
						double precision = relevant / retrieved;
						double recall = 0;
						if(positional.size() != 0){
							recall = relevant / positional.size();
						}
						
						//keeping weight of precision twice of recall so (beta = 0.5)
						//System.out.println(precision + " " + recall);
						double fmeasure = ((1 + 0.5*0.5)*(precision * recall)) / (((0.5*0.5) * precision) + recall);
						
						for (int i = 0; i < size; i++) {
							System.out.println(i + 1 + " " + result.get(i).title + " " + result.get(i).id);
							System.out.println(index_to_doc.get(result.get(i).id).substring(0, index_to_doc.get(result.get(i).id).split(" ").length/10)+"....... ");
							 
						}
						finalFmeasure+=fmeasure;
						finalmrr+=result.get(0).tfidf();
						finalmap+=precision;
						System.out.println("FMeasure : " + fmeasure);
						System.out.println("MRR : " + result.get(0).tfidf());
						System.out.println("Precision : "+precision);
	
						//System.out.println(positional.size());
					}
					
					else{
						System.out.println("Your search - "+query+"  - did not match any documents.");
						System.out.println("Suggestions:");
						System.out.println("\tMake sure that all words are spelled correctly.");
						System.out.println("\tTry different keywords");
						System.out.println("\tTry more general keywords.");
						System.out.println("\tTry fewer keywords");
					}
				}
				else if(number==4){
					break;
				}
				else{
					System.out.println();
					System.out.println("Please enter a valid input");
					System.out.println();
				}
				System.out.println();
				System.out.println("Enter the query : ");
				query=input.readLine();
				System.out.println();
				
			}
			/*AVERAGE DATA OF Fmeasure,MRR AND MAP FOR THE QUERIES THE USER ENTERS*/
			System.out.println("Fmeasure : " + (finalFmeasure/ 20));
			System.out.println("MRR : " + (finalmrr/ 20));
			System.out.println("MAP : " + (finalmap/ 20));
		}
		/*
		 * Proximity Query can take any number of words and an integer between every two words 
		 * The documents conataining those words and with atMax the given distance between them will
		 * be printed
		 */
		else if(querynum==2){	//querynum is 2 for proximity query search
			/*
			 * proxyowrds is the arraylist of words and dist is the arraylist of distances between 
			 * the corresponding words
			 */
				int counter=1;
				BufferedReader q=new BufferedReader(new InputStreamReader(System.in));
				ArrayList<String> proxywords=new ArrayList<String>();
				ArrayList<Integer> dist=new ArrayList<Integer>();
				System.out.println("Enter word number "+counter++ +" : ");
				proxywords.add(q.readLine());
				System.out.println("Enter word number "+counter++ +" : ");
				proxywords.add(q.readLine());
				System.out.println("Enter the maximum distance between words"+(counter-2)+" and "+(counter-1)+" : ");
				dist.add(Integer.parseInt(q.readLine()));
				while(true){
					System.out.println("Press 1 if you want to add more words and 0 instead");
					int num=Integer.parseInt(q.readLine());
					if(num==0)
						break;
					else{
						System.out.println("Enter word number "+counter++ +" : ");
						proxywords.add(q.readLine());
						System.out.println("Enter the maximum distance between words "+(counter-2)+" and "+(counter-1)+" : ");
						dist.add(Integer.parseInt(q.readLine()));
					}
					
				}
				
				/*
				 * words are corrected and then normalized
				 */
				for (int i = 0; i < proxywords.size(); i++) {
					proxywords.set(i, s.add(corrector.correct(proxywords.get(i))));
				}
				
				/*
				 * Now fetching the docs after ANDing the posting list
				 * finalds is the ANDed arraylist of docs
				 * finalsd is the helper arraylist
				 */
				
				ArrayList<doc> finalds = new ArrayList<doc>();
				ArrayList<doc> finalsd = new ArrayList<doc>();
				finalds=token_hash.get(proxywords.get(0));
				if(finalds==null){
					System.out.println("word not found : Please try again. ");
					return;
				}
				for (int i = 1; i < proxywords.size(); i++) {
					ArrayList<doc> temp2 = token_hash.get(proxywords.get(i));
					if(temp2!=null)
					for(int k=0;k<temp2.size();k++){
						
						doc dtemp = temp2.get(k);
						int did = dtemp.id;
						doc rtemp = binary_search(finalds, did);
						if(rtemp!=null){
							finalsd.add(dtemp);
						}
					}
					finalds = finalsd;
					finalsd=new ArrayList<doc>();
				}
				
				
				ArrayList<doc> results=new ArrayList<doc>();	//Resultant documents
				
				/*
				 * Iterating on each doc in finalds and checking if every corresponding words
				 * in proxywords arraylist satisfy the criterea for the doc to be in result using dist
				 * arraylist values
				 */
				
				for (int i = 0; i < finalds.size(); i++) {
					int numb=0;
					int flag=0;
					for (int j = 0; j < proxywords.size()-1; j++) {
						flag=0;
						doc s1=binary_search(token_hash.get(proxywords.get(j)),finalds.get(i).id);
						doc s2=binary_search(token_hash.get(proxywords.get(j+1)),finalds.get(i).id);
						for (int k = 0; k < s1.pos.size(); k++) {
							int place=s1.pos.get(k);
							/*
							 * searching for (place + dist.get(j)) upto (place+1) positioned word 
							 * in pos arraylist of the docs. 
							 * 
							 */
							for (int l = place+dist.get(j); l>place ; l--) {
								int d=binary_search_pos(s2.pos, l);
								if(d!= -1 ){
									flag=1;
									break;
								}
							}
							if(flag==1){
								break;
							}
							
						}
						if(flag==0){
							break;
						}
					}
					if(flag==1){
						results.add(finalds.get(i));
					}
				}
				/*
				 * Printing the titles of retrieved docs and their id(NEWID in doc)
				 */
				for (int i = 0; i < results.size(); i++) {
					System.out.println(results.get(i).title + " " + results.get(i).id);
				}
				
			
		}
		else{
			System.out.println("Wrong option, try again ");
		}
		
	}

}


