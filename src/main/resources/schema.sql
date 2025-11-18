-- 经典典籍表
CREATE TABLE IF NOT EXISTS classic_book (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL, -- 典籍名称
    author TEXT, -- 作者
    dynasty TEXT, -- 朝代
    category TEXT NOT NULL, -- 分类
    intro TEXT -- 简介
);

-- 经典条文表
CREATE TABLE IF NOT EXISTS classic_text (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    book_id INTEGER NOT NULL, -- 典籍ID
    chapter TEXT, -- 章节
    section TEXT, -- 条文号
    content TEXT NOT NULL, -- 原文内容
    translation TEXT, -- 白话文翻译
    interpretation TEXT, -- 解析
    tags TEXT, -- 标签
    FOREIGN KEY (book_id) REFERENCES classic_book(id)
);

-- 中药表
CREATE TABLE IF NOT EXISTS chinese_medicine (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL, -- 中药名称
    pinyin TEXT NOT NULL, -- 拼音
    Latin TEXT, -- 拉丁名
    property TEXT, -- 性味
    channel TEXT, -- 归经
    function TEXT, -- 功效
    indication TEXT, -- 主治
    usage TEXT, -- 用法用量
    caution TEXT, -- 使用注意
    image_url TEXT -- 图片URL
);

-- 中药药证表
CREATE TABLE IF NOT EXISTS medicine_syndrome (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    medicine_id INTEGER NOT NULL, -- 中药ID
    syndrome TEXT NOT NULL, -- 药证
    FOREIGN KEY (medicine_id) REFERENCES chinese_medicine(id)
);

-- 方剂表
CREATE TABLE IF NOT EXISTS prescription (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL, -- 方剂名称
    pinyin TEXT NOT NULL, -- 拼音
    source TEXT, -- 出处
    category TEXT, -- 分类
    composition TEXT, -- 组成
    dosage TEXT, -- 用法用量
    function TEXT, -- 功用
    indication TEXT, -- 主治
    explanation TEXT, -- 方解
    syndromes TEXT -- 方证要点
);

-- 方剂组成表
CREATE TABLE IF NOT EXISTS prescription_medicine (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    prescription_id INTEGER NOT NULL, -- 方剂ID
    medicine_id INTEGER NOT NULL, -- 中药ID
    dosage TEXT NOT NULL, -- 剂量
    role TEXT, -- 君臣佐使
    FOREIGN KEY (prescription_id) REFERENCES prescription(id),
    FOREIGN KEY (medicine_id) REFERENCES chinese_medicine(id)
);

-- 类方关系表
CREATE TABLE IF NOT EXISTS prescription_relation (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    prescription_id INTEGER NOT NULL, -- 方剂ID
    related_id INTEGER NOT NULL, -- 关联方剂ID
    relation_type TEXT NOT NULL, -- 关系类型
    FOREIGN KEY (prescription_id) REFERENCES prescription(id),
    FOREIGN KEY (related_id) REFERENCES prescription(id)
);

-- 脉诊表
CREATE TABLE IF NOT EXISTS pulse_diagnosis (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL, -- 脉象名称
    pinyin TEXT NOT NULL, -- 拼音
    description TEXT NOT NULL, -- 脉象描述
    mechanism TEXT, -- 病机
    image_url TEXT, -- 图片URL
    syndromes TEXT, -- 相关证候
    prescriptions TEXT -- 相关方剂
);

-- 舌诊表
CREATE TABLE IF NOT EXISTS tongue_diagnosis (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tongue_coating TEXT, -- 舌苔
    tongue_body TEXT, -- 舌质
    description TEXT, -- 描述
    mechanism TEXT, -- 病机
    image_url TEXT, -- 图片URL
    syndromes TEXT, -- 相关证候
    prescriptions TEXT -- 相关方剂
);

-- 测试数据
-- 经典典籍
INSERT OR IGNORE INTO classic_book (name, author, dynasty, category, intro) VALUES
('伤寒论', '张仲景', '东汉', '伤寒', '中医经典著作，创立辨证论治体系'),
('金匮要略', '张仲景', '东汉', '杂病', '论述杂病辨证论治的专著'),
('黄帝内经', '佚名', '战国至秦汉', '基础理论', '中医基础理论的奠基之作');

-- 经典条文
INSERT OR IGNORE INTO classic_text (book_id, chapter, section, content, translation, interpretation, tags) VALUES
(1, '太阳病上篇', '第1条', '太阳之为病，脉浮，头项强痛而恶寒。', '太阳病的主要表现是脉浮，头痛项强，并且怕冷。', '太阳病提纲，指出太阳病的主症和主脉。', '太阳病,脉浮,恶寒'),
(1, '太阳病上篇', '第11条', '病有发热恶寒者，发于阳也；无热恶寒者，发于阴也。', '生病时如果有发热又怕冷的，是阳证；如果只有怕冷而没有发热的，是阴证。', '阴阳辨证的重要依据。', '发热,恶寒,阴阳辨证');

-- 中药
INSERT OR IGNORE INTO chinese_medicine (name, pinyin, Latin, property, channel, function, indication, usage, caution, image_url) VALUES
('桂枝', 'guizhi', 'Ramulus Cinnamomi', '辛、甘，温', '心、肺、膀胱经', '发汗解肌，温通经脉，助阳化气', '风寒感冒，脘腹冷痛，血寒经闭，关节痹痛，痰饮，水肿', '煎服，3-10g', '温热病及阴虚阳盛之证、血证、孕妇忌用', 'guizhi.jpg'),
('芍药', 'shaoyao', 'Radix Paeoniae Alba', '苦、酸，微寒', '肝、脾经', '养血调经，敛阴止汗，柔肝止痛，平抑肝阳', '血虚萎黄，月经不调，自汗，盗汗，胁痛，腹痛，四肢挛痛，头痛眩晕', '煎服，6-15g', '不宜与藜芦同用', 'shaoyao.jpg');

-- 方剂
INSERT OR IGNORE INTO prescription (name, pinyin, source, category, composition, dosage, function, indication, explanation, syndromes) VALUES
('桂枝汤', 'guizhitang', '《伤寒论》', '解表剂', '桂枝、芍药、甘草、生姜、大枣', '桂枝9g，芍药9g，炙甘草6g，生姜9g，大枣3枚', '解肌发表，调和营卫', '外感风寒表虚证。头痛发热，汗出恶风，鼻鸣干呕，苔白不渴，脉浮缓或浮弱。', '桂枝为君，发汗解肌，温通经脉；芍药为臣，敛阴和营，与桂枝相配，调和营卫；炙甘草为佐使，调和诸药。', '头痛发热，汗出恶风，脉浮缓'),
('麻黄汤', 'mahuangtang', '《伤寒论》', '解表剂', '麻黄、桂枝、杏仁、炙甘草', '麻黄9g，桂枝6g，杏仁9g，炙甘草3g', '发汗解表，宣肺平喘', '外感风寒表实证。恶寒发热，头身疼痛，无汗而喘，舌苔薄白，脉浮紧。', '麻黄为君，发汗解表，宣肺平喘；桂枝为臣，助麻黄发汗解表；杏仁为佐，降利肺气以助平喘；炙甘草为使，调和诸药。', '恶寒发热，无汗而喘，脉浮紧');

-- 方剂组成
INSERT OR IGNORE INTO prescription_medicine (prescription_id, medicine_id, dosage, role) VALUES
(1, 1, '9g', '君'),
(1, 2, '9g', '臣');

-- 脉诊
INSERT OR IGNORE INTO pulse_diagnosis (name, pinyin, description, mechanism, image_url, syndromes, prescriptions) VALUES
('浮脉', 'fumai', '轻取即得，重按稍减而不空，举之有余，按之不足。', '表证，亦主虚证', 'fumai.jpg', '表证', '桂枝汤,麻黄汤'),
('弦脉', 'xianmai', '端直以长，如按琴弦。', '肝胆病，痰饮，疼痛，疟疾', 'xianmai.jpg', '肝胆病,疼痛', '小柴胡汤,逍遥散');
