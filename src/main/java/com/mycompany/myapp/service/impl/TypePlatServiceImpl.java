package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.TypePlat;
import com.mycompany.myapp.repository.TypePlatRepository;
import com.mycompany.myapp.service.TypePlatService;
import com.mycompany.myapp.service.dto.TypePlatDTO;
import com.mycompany.myapp.service.mapper.TypePlatMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TypePlat}.
 */
@Service
@Transactional
public class TypePlatServiceImpl implements TypePlatService {

    private final Logger log = LoggerFactory.getLogger(TypePlatServiceImpl.class);

    private final TypePlatRepository typePlatRepository;

    private final TypePlatMapper typePlatMapper;

    public TypePlatServiceImpl(TypePlatRepository typePlatRepository, TypePlatMapper typePlatMapper) {
        this.typePlatRepository = typePlatRepository;
        this.typePlatMapper = typePlatMapper;
    }

    @Override
    public TypePlatDTO save(TypePlatDTO typePlatDTO) {
        log.debug("Request to save TypePlat : {}", typePlatDTO);
        TypePlat typePlat = typePlatMapper.toEntity(typePlatDTO);
        typePlat = typePlatRepository.save(typePlat);
        return typePlatMapper.toDto(typePlat);
    }

    @Override
    public Optional<TypePlatDTO> partialUpdate(TypePlatDTO typePlatDTO) {
        log.debug("Request to partially update TypePlat : {}", typePlatDTO);

        return typePlatRepository
            .findById(typePlatDTO.getId())
            .map(existingTypePlat -> {
                typePlatMapper.partialUpdate(existingTypePlat, typePlatDTO);

                return existingTypePlat;
            })
            .map(typePlatRepository::save)
            .map(typePlatMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TypePlatDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TypePlats");
        return typePlatRepository.findAll(pageable).map(typePlatMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TypePlatDTO> findOne(Long id) {
        log.debug("Request to get TypePlat : {}", id);
        return typePlatRepository.findById(id).map(typePlatMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete TypePlat : {}", id);
        typePlatRepository.deleteById(id);
    }
}
