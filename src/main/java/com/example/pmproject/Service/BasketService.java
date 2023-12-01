package com.example.pmproject.Service;

import com.example.pmproject.DTO.BasketDTO;
import com.example.pmproject.Entity.Basket;
import com.example.pmproject.Entity.Member;
import com.example.pmproject.Entity.Product;
import com.example.pmproject.Repository.BasketRepository;
import com.example.pmproject.Repository.MemberRepository;
import com.example.pmproject.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class BasketService {

    private final BasketRepository basketRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    public Page<BasketDTO> basketDTOS(String memberName, Pageable pageable) {
        int page = pageable.getPageNumber()-1;
        int pageLimit = 10;

        Page<Basket> paging;

        if(!Objects.equals(memberName, "")) {
            paging = basketRepository.findByMemberNameList(memberName, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "basketId")));
        }else {
            paging = basketRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "basketId")));
        }

        return paging.map(basket -> BasketDTO.builder()
                .basketId(basket.getBasketId())
                .product(basket.getProduct())
                .quantity(basket.getQuantity())
                .img(basket.getProduct().getImg())
                .member(basket.getMember())
                .tPrice(0)
                .build());
    }

    public void addToCart(String name, Integer quantity, Long productId) {
        Product product = productRepository.findById(productId).orElseThrow();
        Member member = memberRepository.findByName(name).orElseThrow();

        Basket basket = Basket.builder()
                .member(member)
                .product(product)
                .price(product.getPrice())
                .quantity(quantity)
                .build();

        validateDuplicateBasket(basket);
        basketRepository.save(basket);
    }

    private void validateDuplicateBasket(Basket basket) {
        basketRepository.findByMemberAndProduct(basket.getMember(), basket.getProduct()).ifPresent(existingBasket -> {
            throw new IllegalStateException("이미 장바구니에 담겨 있는 상품입니다.");
        });
    }

    public Integer calculateTotalPrice(Integer quantity, Integer price) {
        return quantity*price;
    }

    public Integer modifyBasket(Integer quantity, Long basketId) {
        Basket basket = basketRepository.findById(basketId).orElseThrow();
        Integer price = basket.getPrice();

        Integer newTPrice = calculateTotalPrice(quantity, price);

        basketRepository.modifyQuantity(quantity, basketId);

        return newTPrice;
    }

    public void delete(Long basketId) {
        basketRepository.deleteById(basketId);
    }

}
