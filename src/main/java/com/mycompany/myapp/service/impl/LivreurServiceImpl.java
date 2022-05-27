package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Livreur;
import com.mycompany.myapp.repository.LivreurRepository;
import com.mycompany.myapp.service.LivreurService;
import com.mycompany.myapp.service.dto.LivreurDTO;
import com.mycompany.myapp.service.mapper.LivreurMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Livreur}.
 */
@Service
@Transactional
public class LivreurServiceImpl implements LivreurService {

    private final Logger log = LoggerFactory.getLogger(LivreurServiceImpl.class);

    private final LivreurRepository livreurRepository;

    private final LivreurMapper livreurMapper;

    public LivreurServiceImpl(LivreurRepository livreurRepository, LivreurMapper livreurMapper) {
        this.livreurRepository = livreurRepository;
        this.livreurMapper = livreurMapper;
    }

    @Override
    public LivreurDTO save(LivreurDTO livreurDTO) {
        log.debug("Request to save Livreur : {}", livreurDTO);
        Livreur livreur = livreurMapper.toEntity(livreurDTO);
        livreur = livreurRepository.save(livreur);
        return livreurMapper.toDto(livreur);
    }

    @Override
    public Optional<LivreurDTO> partialUpdate(LivreurDTO livreurDTO) {
        log.debug("Request to partially update Livreur : {}", livreurDTO);

        return livreurRepository
            .findById(livreurDTO.getId())
            .map(existingLivreur -> {
                livreurMapper.partialUpdate(existingLivreur, livreurDTO);

                return existingLivreur;
            })
            .map(livreurRepository::save)
            .map(livreurMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LivreurDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Livreurs");
        return livreurRepository.findAll(pageable).map(livreurMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LivreurDTO> findOne(Long id) {
        log.debug("Request to get Livreur : {}", id);
        return livreurRepository.findById(id).map(livreurMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Livreur : {}", id);
        livreurRepository.deleteById(id);
    }
}
