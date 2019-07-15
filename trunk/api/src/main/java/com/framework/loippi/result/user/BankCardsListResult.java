package com.framework.loippi.result.user;

import com.framework.loippi.entity.user.RdMmBank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 打赏积分
 * Created by Administrator on 2018/1/4.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankCardsListResult {
    /** 银行卡ID */
    private Integer id;
    /** 银行名称 */
    private String bankDetail;
    /** 卡号 */
    private String accCode;
    /** 是否是默认提现卡 1是 0不是 */
    private Integer defaultbank;


    public static List<BankCardsListResult> build(List<RdMmBank> rdMmBankList) {
        List<BankCardsListResult> bankCardsListResultList=new ArrayList<>();
        if (rdMmBankList!=null && rdMmBankList.size()>0){
            for (RdMmBank item:rdMmBankList) {
                BankCardsListResult bankCardsListResult=new BankCardsListResult();
                bankCardsListResult.setBankDetail(item.getBankDetail());
                //银行卡号做处理 例如****     ****     ****     1234
                String info="****     ****     ****     "+item.getAccCode().substring(item.getAccCode().length()-4);
                bankCardsListResult.setAccCode(info);
                bankCardsListResult.setDefaultbank(item.getDefaultbank());
                bankCardsListResult.setId(item.getOid());
                bankCardsListResultList.add(bankCardsListResult);
            }
        }
        return bankCardsListResultList;
    }
}
