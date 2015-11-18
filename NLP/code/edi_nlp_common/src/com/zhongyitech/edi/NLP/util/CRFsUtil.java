package com.zhongyitech.edi.NLP.util;

import java.util.ArrayList;
import java.util.List;

import org.ansj.domain.Term;

import com.zhongyitech.edi.NLP.model.Opinion;
import com.zhongyitech.edi.NLP.model.SingleWord;

public class CRFsUtil {
	public static void makeCRFsTrainSet(List<Opinion> oplist, String w, List<Term> list) throws Exception{
		
		StringBuffer output = new StringBuffer();
		String path = "corpus/CRFsTrainSet.data";
		List<SingleWord> words = new ArrayList<SingleWord>();
//		标注专属词
		for(int i=0;i<w.length();i++){
			SingleWord sw = new SingleWord();
			sw.setWord(w.substring(i,i+1));
			for(int j = 0;j<oplist.size();j++){
				if(i<oplist.get(j).getAspect().getStart_index()){
					sw.setSpec_tag("O");
					break;
				}else if(i==oplist.get(j).getAspect().getStart_index()){
					sw.setSpec_tag("B");
					break;
				}
				else if(i>oplist.get(j).getAspect().getStart_index()&&i<=oplist.get(j).getAspect().getEnd_index()){
					sw.setSpec_tag("I");
					break;
				}
				if(sw.getSpec_tag()==null){
					sw.setSpec_tag("O");
				}
			}
			
			words.add(sw);
		}
		
//		标注词性
		int itag = 0;
		for(int j=0;j<list.size();j++){
			String tempstr = list.get(j).toString();
			int temp = tempstr.indexOf("/");
			for(int i=0;i<temp;i++){
				if(temp==1){
					words.get(itag++).setPos_tag("S"+tempstr.substring(temp+1));
				}else{
					if(i==0){
						words.get(itag++).setPos_tag("B"+tempstr.substring(temp+1));
					}else if(i==temp-1){
						words.get(itag++).setPos_tag("E"+tempstr.substring(temp+1));
					}else{
						words.get(itag++).setPos_tag("M"+tempstr.substring(temp+1));
					}
				}
			}
		}
		
		for(int i=0;i<words.size();i++){
			output.append(words.get(i).getWord());
			output.append("\t");
			output.append(words.get(i).getPos_tag());
			output.append("\t");
			output.append(words.get(i).getSpec_tag());
			output.append("\r\n");
		}
		IoUtil.writeToText(output.toString(),path);
		
		return;
	}

}
