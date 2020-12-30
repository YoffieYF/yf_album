//
//  WFAlbumChooseView.m
//  Wolf
//
//  Created by Yoffie on 2020/8/19.
//  Copyright © 2020 com.mewe.party. All rights reserved.
//

#import "WFAlbumChooseView.h"
#import "WFAlbumChooseCell.h"
#import "WFMacro.h"

@interface WFAlbumChooseView ()<UITableViewDelegate, UITableViewDataSource>

@property (nonatomic, strong) UITableView * tableView;

@end


@implementation WFAlbumChooseView

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self setupViews];
    }
    return self;
}

- (void)dealloc{
    self.tableView.delegate = nil;
    self.tableView.dataSource = nil;
}

#pragma mark - SetupViews
- (void)setupViews {
   
    self.backgroundColor = WF_STR_AHEX(@"#4F4F4F");
    
    [self addSubview:self.tableView];
    [self.tableView setFrame:self.bounds];
}

#pragma mark - Function
- (void)showInView:(UIView *)view {
    if (!view) {
        return;
    }
    
    [UIView animateWithDuration:0.25 animations:^{
        self.y = 60;
        //self.y = scale375_value(WFNavigationbarHeight);
    } completion:^(BOOL finished) {
    }];
}

- (void)dismiss {
    [UIView animateWithDuration:0.25 animations:^{
        self.y = - WF_SCREEN_HEIGHT;
    } completion:^(BOOL finished) {
    }];
}

#pragma mark --- UITableViewDelegate, UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _albumArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    WFAlbumChooseCell *cell = [self.tableView dequeueReusableCellWithIdentifier:kWFAlbumChooseCellIdentifier];
    cell.albumModel = _albumArray[indexPath.row];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    WFAlbumModel *oldModel = [WFAlbumModel getSelectAlbumModelInModels:_albumArray];
    oldModel.selected = NO;
    WFAlbumModel *selectModel = self.albumArray[indexPath.item];
    selectModel.selected = YES;
    if (self.delegate && [self.delegate respondsToSelector:@selector(selectAlbum: withSelectAlbumModel: withOldAlbumModel:)]) {
        [self.delegate selectAlbum:self withSelectAlbumModel:selectModel withOldAlbumModel:oldModel];
    }
    [self.tableView reloadData];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 60.f;
}

#pragma mark - Parameter Get
- (UITableView *)tableView {
    if (!_tableView) {
        _tableView = [[UITableView alloc] initWithFrame:CGRectZero style:UITableViewStylePlain];
        _tableView.delegate = self;
        _tableView.dataSource = self;
        _tableView.bounces = NO;
        _tableView.backgroundColor = WF_STR_AHEX(@"#4F4F4F");
        _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
        _tableView.showsVerticalScrollIndicator = NO;
        _tableView.showsHorizontalScrollIndicator = NO;
        [_tableView registerClass:[WFAlbumChooseCell class] forCellReuseIdentifier:kWFAlbumChooseCellIdentifier];
    }
    return _tableView;
}

#pragma mark - Parameter Set
- (void)setAlbumArray:(NSMutableArray<WFAlbumModel *> *)albumArray {
    if(!albumArray) {
        return;
    }
    if(!_albumArray) {
        _albumArray = [[NSMutableArray alloc] init];
    }
    _albumArray = albumArray;
    [self.tableView reloadData];
}

@end
