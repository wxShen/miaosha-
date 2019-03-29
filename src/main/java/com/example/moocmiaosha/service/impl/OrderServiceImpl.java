package com.example.moocmiaosha.service.impl;

import com.example.moocmiaosha.dao.OrderDOMapper;
import com.example.moocmiaosha.dao.SequenceDOMapper;
import com.example.moocmiaosha.datapobject.OrderDO;
import com.example.moocmiaosha.datapobject.SequenceDO;
import com.example.moocmiaosha.error.BusinessException;
import com.example.moocmiaosha.error.EmBusinessError;
import com.example.moocmiaosha.service.ItemService;
import com.example.moocmiaosha.service.OrderService;
import com.example.moocmiaosha.service.UserService;
import com.example.moocmiaosha.service.model.ItemModel;
import com.example.moocmiaosha.service.model.OrderModel;
import com.example.moocmiaosha.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {


    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    private OrderDO convertFromOrderModel(OrderModel orderModel){
        if(orderModel == null){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel,orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderDO;
    }

    @Override
    @Transactional  //保证事物的原子性
    public OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount) throws BusinessException {

        //1.校验下单状态，下单的商品是否存在，用户是否合法，购买数量是否正确
        ItemModel itemModel = itemService.getItemById(itemId);
        if(itemModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDTION_ERROR,"商品信息不存在");
        }

        UserModel userModel = userService.getUserbyId(userId);
        if(userModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDTION_ERROR,"用户信息不存在");
        }

        if(amount <= 0 || amount > 99){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDTION_ERROR,"商品数量不正确");
        }

        //校验活动信息
        if(promoId != null){
            //(1)校验对应活动是否存在这个适用商品
            if(promoId.intValue() != itemModel.getPromoModel().getId()){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDTION_ERROR,"活动信息不正确");
            }else if(itemModel.getPromoModel().getStatus().intValue() != 2){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDTION_ERROR,"活动还未开始");
            }
        }

        //2.落单减库存，支付减库存
        boolean result = itemService.decreaseStock(itemId,amount);

        if(!result){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }
        //3.订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setUserId(userId);
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        if(promoId != null){
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else{
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setPromoId(promoId);
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));

        //生成交易流水号第，订单号
        orderModel.setId(generateOrderNo());
        OrderDO orderDO = this.convertFromOrderModel(orderModel);
        orderDOMapper.insertSelective(orderDO);

        //加上商品的销量
        itemService.increaseSales(itemId,amount);
        //返回前端
        return orderModel;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected String generateOrderNo(){
        //订单号有16位
        StringBuilder stringBuilder = new StringBuilder();
        //前八位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
        stringBuilder.append(nowDate);
        //中间六位为自增序列
        //获取当前sequence
        int sequence = 0;
        SequenceDO sequenceDO = sequenceDOMapper.getSequenceByName("order_info");
        sequence = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKeySelective(sequenceDO);
        String sequenceStr = String.valueOf(sequence);
        for(int i = 0; i < 6 -sequenceStr.length(); i++){ //不足六位时，前面填零
            stringBuilder.append(0);
        }
        stringBuilder.append(sequenceStr);
        //最后两位为分库分表位，暂时写死
        stringBuilder.append("00");
        return stringBuilder.toString();
    }

}
